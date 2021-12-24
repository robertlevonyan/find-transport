package robert.findtransport.presentation.track

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.base.MainActivity
import robert.findtransport.databinding.FragmentTrackRouteBinding
import robert.findtransport.presentation.component.dialog.NextStopDialog
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding
import java.util.*

@AndroidEntryPoint
class TrackRouteFragment : BaseFragment<TrackRouteViewModel, FragmentTrackRouteBinding>() {
  override val binding: FragmentTrackRouteBinding by viewBinding(FragmentTrackRouteBinding::inflate)
  override val viewModel: TrackRouteViewModel by viewModels()

  private var isBound: Boolean = false
  private var trackRouteService: TrackRouteService? = null

  private val serviceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      val binder = service as TrackRouteService.TrackRouteBinder
      trackRouteService = binder.getService()
      onServiceBound()
      trackRouteService?.isBound = true
      isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      trackRouteService?.isBound = false
      isBound = false
    }
  }

  private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) {
      initData()
    } else {
      router.exit()
      context?.showToast(getString(R.string.error_location))
    }
  }

  override fun FragmentTrackRouteBinding.initInsets() {
    btnStop.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    context?.run {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
      } else {
        initData()
      }
    }
  }

  private fun initData() {
    if (activity == null || activity !is MainActivity) {
      return
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        (activity as? MainActivity)?.resumedState?.collect { resumed ->
          if (!resumed) return@collect

          startTrackerService()
        }
      }
    }
  }

  private fun startTrackerService() {
    val transportId = arguments?.getInt(ARG_TRANSPORT_ID) ?: 0
    val fromId = arguments?.getInt(ARG_FROM_ID) ?: 0
    val toId = arguments?.getInt(ARG_TO_ID) ?: 0

    Intent(context, TrackRouteService::class.java)
      .apply {
        putExtra(EXTRA_TRANSPORT_ID, transportId)
        putExtra(EXTRA_FROM, fromId)
        putExtra(EXTRA_TO, toId)
      }
      .also { intent ->
        context?.applicationContext?.run {
          startService(intent)
          if (isBound && trackRouteService?.isBound == true) {
            unbindService(serviceConnection)
          }
          try {
            isBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
          } catch (e: Exception) {
            e.printStackTrace()
            router.exit()
          }
        }
      }
  }

  override fun FragmentTrackRouteBinding.initViews() {
    root.setOnClickListener { }
    btnStop.setOnClickListener {
      stopTracker()
      router.exit()
    }
  }

  private fun onServiceBound() = trackRouteService?.run {
    if (activity == null || activity?.hasWindowFocus() == false) {
      return@run
    }
    collectWithLifecycle(selectedTransport) { transport ->
      binding.progressLoading.visibility = View.GONE

      val typeNameRes = transport.getTypeName()
      val typeName = if (typeNameRes == -1) "" else getString(typeNameRes).lowercase(Locale.ROOT)
      val label = getString(R.string.label_tracker_transport)
      binding.tvLabelSelected.text = buildSpannedString {
        append(label)
        bold {
          append(" ")
          append(typeName)
          append(" ")
          append(transport.number)
        }
      }
    }
    collectWithLifecycle(toStop) {

    }
    collectWithLifecycle(fromStop) {

    }
    collectWithLifecycle(currentStop) {
      binding.tvCurrentStop.isVisible = true
      binding.tvCurrentStopName.setStopName(it, currentLanguage)
      binding.progressLoading.visibility = View.GONE
    }
    collectWithLifecycle(predestination) { }
    collectWithLifecycle(notifyNextStop) {
      if (activity?.isFinishing != true) {
        NextStopDialog.newInstance().show(parentFragmentManager, NextStopDialog::class.java.simpleName)
      }
    }
    collectWithLifecycle(notifyArrived) {
      parentFragmentManager.setFragmentResult(RESULT_ARRIVED, bundleOf())
      router.exit()
    }
    collectWithLifecycle(notifyStop) {
      router.exit()
    }
  }

  private fun stopTracker() {
    try {
      trackRouteService?.run {
        if (isBound && trackRouteService?.isBound == true) {
          this@TrackRouteFragment.context?.applicationContext?.unbindService(serviceConnection)
        }
        stopForeground(true)
        stopSelf()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onDestroyView() {
    stopTracker()
    super.onDestroyView()
  }

  companion object {
    fun newInstance(args: Bundle) = TrackRouteFragment().apply {
      arguments = args
    }
  }
}
