package robert.findtransport.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentMapBinding
import robert.findtransport.presentation.component.dialog.LocationPermissionDialog
import robert.findtransport.utils.RESULT_LOCATION_PERMISSION
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

abstract class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding>() {
  override val binding: FragmentMapBinding by viewBinding(FragmentMapBinding::inflate)
  override val viewModel: MapViewModel by viewModel()

  private var locationEnabled = false
  protected val mapboxMap by lazy { binding.mapView.getMapboxMap() }

  private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
    binding.fabLocation.visibility = if (permissions.all { it.value }) {
      locationEnabled = true
      setFragmentResult(RESULT_LOCATION_PERMISSION, bundleOf())
      View.VISIBLE
    } else {
      locationEnabled = false
      View.GONE
    }
    activity?.run { initMap(this) }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

    activity?.run {
      if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED
      ) {
        showDialogForPermissions(this, permissions)
      } else {
        locationEnabled = true
        initMap(this)
      }
    } ?: router.exit()
  }

  override fun FragmentMapBinding.initInsets() {
    appBar.onWindowInsets { view, windowInsets ->
      view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    fabLocation.onWindowInsets { view, windowInsets ->
      view.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentMapBinding.initViews() {
    fabLocation.setOnClickListener { goToCurrentLocation(binding.mapView.getMapboxMap()) }
  }

  @Suppress("SameParameterValue")
  private fun showDialogForPermissions(activity: FragmentActivity, permissions: Array<String>) =
    LocationPermissionDialog().run {
      positiveClick = { permissionRequest.launch(permissions) }
      negativeClick = {
        binding.fabLocation.visibility = View.GONE
        initMap(activity)
      }
      show(activity.supportFragmentManager, "")
    }

  private fun initMap(activity: FragmentActivity) = binding.run {
    mapView.compass.apply {
      marginLeft = 0f
      marginTop = context?.getDimen(R.dimen.margin_85) ?: 200f
      marginRight = context?.getDimen(R.dimen.fab_margin) ?: 50f
      marginBottom = 0f
    }

    mapView.getMapboxMap().loadStyleUri(getString(R.string.map_style)) { style ->
      createMap(style)
      if (locationEnabled) {
        enableLocationComponent(activity, style)
      } else {
        mapView.camera.flyTo(
          cameraOptions = CameraOptions.Builder()
            .anchor(ScreenCoordinate(40.180982, 44.5114422))
            .zoom(15.0)
            .build(),
          animationOptions = MapAnimationOptions.mapAnimationOptions {
            duration(200)
            interpolator(FastOutSlowInInterpolator())
          }
        )

//        mapView.camera.animate(CameraUpdateFactory.newLatLngZoom(LatLng(40.180982, 44.5114422), 15.0))
      }
    }
  }

  abstract fun createMap(style: Style)

  protected fun hideLoading() {
    binding.flLoading.visibility = View.GONE
  }

  @SuppressLint("MissingPermission")
  private fun enableLocationComponent(activity: FragmentActivity, style: Style) {
//    val customLocationComponentOptions = LocationComponentOptions.builder(activity)
//      .trackingGesturesManagement(true)
//      .accuracyColor(activity.getColorFromRes(R.color.colorAccentTransparent))
//      .foregroundTintColor(activity.getColorFromRes(R.color.colorAccent))
//      .bearingTintColor(activity.getColorFromRes(R.color.colorAccent))
//      .build()

//    val locationComponentActivationOptions = LocationComponentActivationOptions.builder(activity, style)
//      .locationComponentOptions(customLocationComponentOptions)
//      .build()

    binding.mapView.location.updateSettings {
      enabled = true
      pulsingEnabled = true
      locationPuck = LocationPuck2D()
    }

//    mapboxMap?.let { map ->
//      map.locationComponent.run {
//        activateLocationComponent(locationComponentActivationOptions)
//        isLocationComponentEnabled = true
//        cameraMode = CameraMode.TRACKING
//        renderMode = RenderMode.COMPASS
//        goToCurrentLocation(map)
//      }
//    }
  }

  private fun goToCurrentLocation(mapboxMap: MapboxMap) {
//    mapboxMap.locationComponent.run {
//      if (!isLocationComponentActivated) return@run
//      val latitude = lastKnownLocation?.latitude ?: DEFAULT_LATITUDE
//      val longitude = lastKnownLocation?.longitude ?: DEFAULT_LONGITUDE
//      mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15.0))
//    }
  }

  override fun onStart() {
    super.onStart()
    binding.mapView.onStart()
  }

  override fun onStop() {
    binding.mapView.onStop()
    super.onStop()
  }

  override fun onLowMemory() {
    binding.mapView.onLowMemory()
    super.onLowMemory()
  }

  override fun onDestroyView() {
    binding.mapView.onDestroy()

//    mapboxMap?.style?.let { style ->
//      style.sources.forEach { style.removeSource(it) }
//      style.layers.forEach { style.removeLayer(it) }
//    }
    super.onDestroyView()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    const val STOP_IMAGE = "Stop_Image"
    const val METRO_IMAGE = "Metro_Image"
    const val ROUTE_SOURCE = "Route_Source"
    const val ROUTE_LAYER = "Route_Layer"
    const val STOP_ICON_SIZE = 0.12f
    const val STOP_ICON_BIG_SIZE = 0.15f
  }
}
