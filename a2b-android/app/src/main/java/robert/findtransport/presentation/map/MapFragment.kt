package robert.findtransport.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.MapLoadingErrorEventData
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import robert.findtransport.BuildConfig
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.entity.Stop
import robert.findtransport.databinding.FragmentMapBinding
import robert.findtransport.domain.mapper.fromJson
import robert.findtransport.domain.mapper.toStop
import robert.findtransport.presentation.component.dialog.LocationPermissionDialog
import robert.findtransport.utils.DEFAULT_LATITUDE
import robert.findtransport.utils.DEFAULT_LONGITUDE
import robert.findtransport.utils.RESULT_LOCATION_PERMISSION
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

abstract class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding>() {
  override val binding: FragmentMapBinding by viewBinding(FragmentMapBinding::inflate)
  override val viewModel: MapViewModel by viewModels()

  private var locationEnabled = false
  protected var mapboxMap: MapboxMap? = null
  protected var pointAnnotationManager: PointAnnotationManager? = null
  protected var polylineAnnotationManager: PolylineAnnotationManager? = null

  private val permissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.READ_PHONE_STATE,
  )

  private val permissionRequest = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    binding.fabLocation.visibility = if (permissions.all { it.value }) {
      locationEnabled = true
      setFragmentResult(RESULT_LOCATION_PERMISSION, bundleOf())
      viewModel.getCurrentLocation()
      View.VISIBLE
    } else {
      locationEnabled = false
      View.GONE
    }
  }

  private val locationPermissionDialog by lazy {
    LocationPermissionDialog().apply {
      positiveClick = {
        view?.post {
          try {
            permissionRequest.launch(permissions)
          } catch (e: Exception) {
            e.printStackTrace()
            router.exit()
          }
        }
      }
      negativeClick = {
        binding.fabLocation.visibility = View.GONE
        if (!isDetached) {
          initMap()
        }
      }
    }
  }
  private var isDialogShowing = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    activity?.run {
      if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
      ) {
        showDialogForPermissions(this)
      } else {
        locationEnabled = true
        viewModel.getCurrentLocation()
      }

      if (!isDetached) {
        initMap()
      }
    } ?: router.exit()
  }

  override fun FragmentMapBinding.initInsets() {
    appBar.onWindowInsets { view, windowInsets ->
      view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    fabLocation.onWindowInsets { view, windowInsets ->
      view.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom +
          getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentMapBinding.initViews() {
    fabLocation.setOnClickListener { viewModel.getCurrentLocation() }
  }

  private fun showDialogForPermissions(activity: FragmentActivity) {
    if (!activity.isFinishing && !isDialogShowing) {
      isDialogShowing = true
      locationPermissionDialog.show(activity.supportFragmentManager, "")
    }
  }

  private fun initMap() = binding.run {
    binding.flLoading.isVisible = true

    mapView.compass.apply {
      marginLeft = 0f
      marginTop = context?.getDimen(R.dimen.margin_85) ?: 200f
      marginRight = context?.getDimen(R.dimen.fab_margin) ?: 50f
      marginBottom = 0f
    }
    mapView.scalebar.enabled = false
    mapView.gestures.rotateEnabled = false

    mapboxMap = mapView.getMapboxMap()
    pointAnnotationManager = mapView.annotations.createPointAnnotationManager(binding.mapView).apply {
      addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
        if (!isStateSaved) {
          pointAnnotation.getData()?.let { data -> showStopOptions(data.fromJson<Stop>().toStop()) }
        }
        true
      })
    }
    polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager(binding.mapView).apply {
      lineCap = LineCap.ROUND
    }

    try {
      val mapStyle = if (context?.isNightMode() == true) {
        BuildConfig.MAPBOX_STYLE_NIGHT
      } else {
        BuildConfig.MAPBOX_STYLE_LIGHT
      }
      mapboxMap?.loadStyleUri(mapStyle, { style ->
        if (locationEnabled) {
          enableLocationComponent()
        } else {
          flyTo(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        }
        createMap(style)
      },
        object : OnMapLoadErrorListener {
          override fun onMapLoadError(eventData: MapLoadingErrorEventData) {
            println("Map error $eventData")
          }
        })
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  protected fun flyTo(latitude: Double, longitude: Double) {
    try {
      mapboxMap?.flyTo(
        cameraOptions = CameraOptions.Builder()
          .center(Point.fromLngLat(longitude, latitude))
          .zoom(15.0)
          .build(),
        animationOptions = MapAnimationOptions.mapAnimationOptions {
          duration(duration = 200)
          interpolator(interpolator = FastOutSlowInInterpolator())
        },
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  abstract fun createMap(style: Style)

  open fun showStopOptions(stop: robert.findtransport.data.model.Stop) = Unit

  protected fun hideLoading() {
    binding.flLoading.isVisible = false
  }

  private fun enableLocationComponent() {
    val colorRes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      android.R.color.system_accent1_300
    } else {
      R.color.colorAccent300
    }
    binding.mapView.location.updateSettings {
      enabled = true
      pulsingEnabled = false
      pulsingColor = context?.getColorFromRes(colorRes) ?: Color.YELLOW
      locationPuck = LocationPuck2D().apply {
        topImage = BitmapDrawable(resources, context?.getBitmapFromVectorDrawable(R.drawable.ic_bearing))
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }
}
