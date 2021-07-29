package robert.findtransport.presentation.track

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import org.koin.android.ext.android.inject
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.presentation.component.ld.SingleLiveEvent
import robert.findtransport.utils.EXTRA_FROM
import robert.findtransport.utils.EXTRA_TO
import robert.findtransport.utils.EXTRA_TRANSPORT_ID
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getTypeName

class TrackRouteService : Service() {
  private val transportUseCase: TransportUseCase by inject()
  private val stopsUseCase: StopsUseCase by inject()
  private val locationUseCase: LocationUseCase by inject()
  private val localeUseCase: LocaleUseCase by inject()

  private val trackRouteScope = CoroutineScope(Dispatchers.IO)
  private val binder = TrackRouteBinder()
  var isBound: Boolean = false

  private val _selectedTransport = SingleLiveEvent<Transport>()
  val selectedTransport: LiveData<Transport> get() = _selectedTransport

  private val _fromStop = SingleLiveEvent<Stop>()
  val fromStop: LiveData<Stop> get() = _fromStop

  private val _toStop = SingleLiveEvent<Stop>()
  val toStop: LiveData<Stop> get() = _toStop

  private val _currentStop = SingleLiveEvent<Stop>()
  val currentStop: LiveData<Stop> get() = _currentStop

  private val _previousStop = SingleLiveEvent<Stop>()
  val previousStop: LiveData<Stop> get() = _previousStop

  private val _predestination = SingleLiveEvent<Stop>()
  val predestination: LiveData<Stop> get() = _predestination

  private val _notifyNextStop = SingleLiveEvent<Stop>()
  val notifyNextStop: LiveData<Stop> get() = _notifyNextStop

  private val _notifyArrived = SingleLiveEvent<Unit>()
  val notifyArrived: LiveData<Unit> get() = _notifyArrived

  private val _notifyStop = SingleLiveEvent<Unit>()
  val notifyStop: LiveData<Unit> get() = _notifyStop

  val currentLanguage: String
    get() = localeUseCase.getCurrentLanguage()

  private val notificationManager by lazy { (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager) }

  override fun onCreate() {
    super.onCreate()
    startNotification()

    trackRouteScope.launch {
      subscribeToLocationChanges().collect(::getNearbyStopNames)
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.hasExtra(EXTRA_TRANSPORT_ID) == true) {
      onHandleIntent(intent)
    }
    if (intent?.action == STOP_ACTION) {
      if (notifyStop.hasActiveObservers() || notifyStop.hasObservers()) {
        _notifyStop.postValue(Unit)
      } else {
        stopForeground(true)
        stopSelf()
      }
    }
    return START_NOT_STICKY
  }

  suspend fun subscribeToLocationChanges() = locationUseCase.subscribeToLocationUpdates().stateIn(trackRouteScope)

  fun getNearbyStopNames(location: Location) {
    val transport = _selectedTransport.value ?: return
    val start = _fromStop.value ?: return
    val destination = _toStop.value ?: return

    trackRouteScope.launch(Dispatchers.IO) {
      transportUseCase.getNearbyStopFromTransport(
        transport = transport,
        start = start,
        destination = destination,
        location = location,
        coroutineScope = trackRouteScope
      ).collect { stops ->
        val current = stops.first
        val predestination = stops.second

        if (current == Stop.EMPTY) return@collect

        _previousStop.postValue(_currentStop.value)
        _currentStop.postValue(current)
        _predestination.postValue(predestination)

        if (_previousStop.value?.id != _currentStop.value?.id) {
          updateNotification(
            notificationTitle = "${getString(R.string.label_tracker_transport)} ${getString(transport.getTypeName())} ${transport.number}",
            notificationText = current.getCurrentName(localeUseCase.getCurrentLanguage()),
          )
        }

        if (current.id == predestination.id && _notifyNextStop.value == null) {
          _notifyNextStop.postValue(predestination)
        }
        if (current.id == destination.id && _notifyArrived.value == null) {
          _notifyArrived.postValue(Unit)
          showArrivedNotification()
          trackRouteScope.cancel()
        }
      }
    }
  }

  private fun createNotification(notificationTitle: String, notificationText: String): Notification =
    NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(notificationTitle)
      .setContentText(notificationText)
      .setSmallIcon(R.drawable.ic_notification)
      .addAction(
        R.drawable.ic_close_black_24dp,
        getString(R.string.label_stop_tracker),
        PendingIntent.getService(
          this,
          1,
          Intent(this, TrackRouteService::class.java).apply { action = STOP_ACTION },
          0
        )
      )
      .build()

  private fun startNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "Route Tracker", NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager?.createNotificationChannel(channel)
    }

    val notification = createNotification(getString(R.string.title_track_route), getString(R.string.title_track_route_start))

    startForeground(NOTIFICATION_ID, notification)
  }

  private fun updateNotification(notificationTitle: String, notificationText: String) {
    val notification = createNotification(notificationTitle, notificationText)

    notificationManager?.notify(NOTIFICATION_ID, notification)
  }

  private fun showArrivedNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_ID, "Route Tracker", NotificationManager.IMPORTANCE_HIGH)
      notificationManager?.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(getString(R.string.title_track_route))
      .setContentText(getString(R.string.label_arrived))
      .setSmallIcon(R.drawable.ic_notification)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .build()

    notificationManager?.notify(ARRIVED_ID, notification)
  }

  override fun onBind(intent: Intent): IBinder {
    onHandleIntent(intent)
    return binder
  }

  override fun onRebind(intent: Intent?) {
    super.onRebind(intent)
    onHandleIntent(intent ?: return)
  }

  private fun onHandleIntent(intent: Intent): Int {
    val transportId: Int = intent.getIntExtra(EXTRA_TRANSPORT_ID, 0)
    val fromId: Int = intent.getIntExtra(EXTRA_FROM, 0)
    val toId: Int = intent.getIntExtra(EXTRA_TO, 0)

    trackRouteScope.launch(Dispatchers.IO) {
      val selectedTransport = async { transportUseCase.getTransportById(transportId) }
      val fromStop = async { stopsUseCase.getStop(fromId) }
      val toStop = async { stopsUseCase.getStop(toId) }

      _fromStop.postValue(fromStop.await())
      _toStop.postValue(toStop.await())
      launch { selectedTransport.await().collect(_selectedTransport::postValue) }
    }

    return START_NOT_STICKY
  }

  override fun onUnbind(intent: Intent?): Boolean {
    return true
  }

  override fun onDestroy() {
    trackRouteScope.cancel()
    super.onDestroy()
  }

  inner class TrackRouteBinder : Binder() {
    fun getService(): TrackRouteService = this@TrackRouteService
  }

  companion object {
    const val CHANNEL_ID = "TrackerChannel"
    const val NOTIFICATION_ID = 100
    const val ARRIVED_ID = 101
    const val STOP_ACTION = "STOP_ACTION"
  }
}