package robert.findtransport.presentation.map

import android.Manifest
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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import org.koin.androidx.viewmodel.ext.android.viewModel
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
  override val viewModel: MapViewModel by viewModel()

  private var locationEnabled = false
  protected val mapboxMap by lazy { binding.mapView.getMapboxMap() }
  protected val pointAnnotationManager by lazy {
    binding.mapView.annotations.createPointAnnotationManager(binding.mapView).apply {
      addClickListener(OnPointAnnotationClickListener { pointAnnotation ->
        if (!isStateSaved) {
          pointAnnotation.getData()?.let { data -> showStopOptions(data.fromJson<Stop>().toStop()) }
        }
        true
      })
    }
  }
  protected val polylineAnnotationManager by lazy {
    binding.mapView.annotations.createPolylineAnnotationManager(binding.mapView).apply {
      lineCap = LineCap.ROUND
    }
  }

  private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
    binding.fabLocation.visibility = if (permissions.all { it.value }) {
      locationEnabled = true
      setFragmentResult(RESULT_LOCATION_PERMISSION, bundleOf())
      View.VISIBLE
    } else {
      locationEnabled = false
      View.GONE
    }
    activity?.run { initMap() }
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
        initMap()
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
    fabLocation.setOnClickListener { goToCurrentLocation() }
  }

  override fun MapViewModel.initObservers() {
    observe(currentLocation) { location ->
      flyTo(location.latitude, location.longitude)
    }
  }

  private fun showDialogForPermissions(activity: FragmentActivity, permissions: Array<String>) =
    LocationPermissionDialog().run {
      positiveClick = { permissionRequest.launch(permissions) }
      negativeClick = {
        binding.fabLocation.visibility = View.GONE
        initMap()
      }
      show(activity.supportFragmentManager, "")
    }

  private fun initMap() = binding.run {
    mapView.compass.apply {
      marginLeft = 0f
      marginTop = context?.getDimen(R.dimen.margin_85) ?: 200f
      marginRight = context?.getDimen(R.dimen.fab_margin) ?: 50f
      marginBottom = 0f
    }

    mapboxMap.loadStyleUri(getString(R.string.map_style)) { style ->
      createMap(style)
      if (locationEnabled) {
        enableLocationComponent()
      } else {
        flyTo(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
      }
    }
  }

  private fun flyTo(latitude: Double, longitude: Double) {
    mapboxMap.flyTo(
      cameraOptions = CameraOptions.Builder()
        .center(Point.fromLngLat(longitude, latitude))
        .zoom(15.0)
        .build(),
      animationOptions = MapAnimationOptions.mapAnimationOptions {
        duration(200)
        interpolator(FastOutSlowInInterpolator())
      },
    )
  }

  abstract fun createMap(style: Style)

  open fun showStopOptions(stop: robert.findtransport.data.model.Stop) = Unit

  protected fun hideLoading() {
    binding.flLoading.visibility = View.GONE
  }

  private fun enableLocationComponent() {
    binding.mapView.location.updateSettings {
      enabled = true
      pulsingEnabled = true
      pulsingColor = binding.mapView.context.getColorFromRes(R.color.colorAccent)
      locationPuck = LocationPuck2D()
    }
  }

  private fun goToCurrentLocation() {
    viewModel.currentLocation.value.let { location ->
      flyTo(location.latitude, location.longitude)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }
}
