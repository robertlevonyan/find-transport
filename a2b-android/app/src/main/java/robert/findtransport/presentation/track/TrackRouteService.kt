package robert.findtransport.presentation.track

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.domain.usecase.location.LocationUseCase
import robert.findtransport.domain.usecase.preference.LocaleUseCase
import robert.findtransport.domain.usecase.stop.StopsUseCase
import robert.findtransport.domain.usecase.transport.TransportUseCase
import robert.findtransport.utils.EXTRA_FROM
import robert.findtransport.utils.EXTRA_TO
import robert.findtransport.utils.EXTRA_TRANSPORT_ID
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getTypeName
import java.util.concurrent.atomic.AtomicInteger

class TrackRouteService : Service() {
  private val transportUseCase: TransportUseCase by inject()
  private val stopsUseCase: StopsUseCase by inject()
  private val locationUseCase: LocationUseCase by inject()
  private val localeUseCase: LocaleUseCase by inject()

  private val trackRouteScope = CoroutineScope(Dispatchers.IO)
  private val binder = TrackRouteBinder()
  var isBound: Boolean = false

  private val _selectedTransport = MutableSharedFlow<Transport>()
  val selectedTransport: Flow<Transport> get() = _selectedTransport

  private val _fromStop = MutableSharedFlow<Stop>()
  val fromStop: Flow<Stop> get() = _fromStop

  private val _toStop = MutableSharedFlow<Stop>()
  val toStop: Flow<Stop> get() = _toStop

  private val _currentStop = MutableSharedFlow<Stop>()
  val currentStop: Flow<Stop> get() = _currentStop

  private val _previousStop = MutableSharedFlow<Stop>()
  val previousStop: Flow<Stop> get() = _previousStop

  private val _predestination = MutableSharedFlow<Stop>()
  val predestination: Flow<Stop> get() = _predestination

  private val _notifyNextStop = MutableSharedFlow<Stop>()
  val notifyNextStop: Flow<Stop> get() = _notifyNextStop

  private val _notifyArrived = MutableSharedFlow<Unit?>()
  val notifyArrived: Flow<Unit?> get() = _notifyArrived

  private val _notifyStop = MutableSharedFlow<Unit>().apply {
    onStart {
      println("notifyStopObservers inc")
      notifyStopObservers.incrementAndGet()
      println("notifyStopObservers inc ${notifyStopObservers.get()}")
    }
    onCompletion {
      println("notifyStopObservers dec")
      notifyStopObservers.decrementAndGet()
      println("notifyStopObservers dec ${notifyStopObservers.get()}")
    }
  }
  val notifyStop: Flow<Unit> get() = _notifyStop

  private val notifyStopObservers = AtomicInteger(0)

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
      println("notifyStopObservers ${notifyStopObservers.get()}")
      if (notifyStopObservers.get() > 0) {
        trackRouteScope.launch { _notifyStop.emit(Unit) }
      } else {
        stopForeground(true)
        stopSelf()
      }
    }
    return START_NOT_STICKY
  }

  private suspend fun subscribeToLocationChanges() = locationUseCase.subscribeToLocationUpdates().stateIn(trackRouteScope)

  private suspend fun getNearbyStopNames(location: Location) {
    println("getNearbyStopNames $location")
    val transport = _selectedTransport.firstOrNull() ?: return
    val start = _fromStop.firstOrNull() ?: return
    val destination = _toStop.firstOrNull() ?: return

    println("getNearbyStopNames $transport, $start, $destination")

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

        println("getNearbyStopNames current $current")

        val currentStopValue = _currentStop.firstOrNull() ?: return@collect
        val previousStopValue = _previousStop.firstOrNull() ?: return@collect

        println("getNearbyStopNames currentStopValue $currentStopValue")
        println("getNearbyStopNames previousStopValue $previousStopValue")

        _previousStop.emit(currentStopValue)
        _currentStop.emit(current)
        _predestination.emit(predestination)

        if (previousStopValue.id != currentStopValue.id) {
          updateNotification(
            notificationTitle = "${getString(R.string.label_tracker_transport)} ${getString(transport.getTypeName())} ${transport.number}",
            notificationText = current.getCurrentName(localeUseCase.getCurrentLanguage()),
          )
        }

        if (current.id == predestination.id) {
          _notifyNextStop.emit(predestination)
        }
        if (current.id == destination.id && _notifyArrived.firstOrNull() == null) {
          _notifyArrived.emit(Unit)
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

      _fromStop.emit(fromStop.await())
      _toStop.emit(toStop.await())
      launch { selectedTransport.await().collect(_selectedTransport::emit) }
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