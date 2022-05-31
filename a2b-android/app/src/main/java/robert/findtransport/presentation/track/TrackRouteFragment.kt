package robert.findtransport.presentation.track

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import robert.findtransport.data.model.Stop
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
//    if (activity == null || activity !is MainActivity) {
//      return
//    }

    lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//        (activity as? MainActivity)?.resumedState?.collect { resumed ->
//          if (!resumed) return@collect
//
//          startTrackerService()
//        }
      }
    }
    initCollectors()
  }

  private fun startTrackerService() {
    val transportId = arguments?.getInt(ARG_TRANSPORT_ID) ?: 0
    val fromId = arguments?.getInt(ARG_FROM_ID) ?: 0
    val toId = arguments?.getInt(ARG_TO_ID) ?: 0

    viewModel.initData(transportId, fromId, toId)
  }

  override fun FragmentTrackRouteBinding.initViews() {
    root.setOnClickListener { }
    btnStop.setOnClickListener {
      onBackPressed()
    }
  }

  private fun initCollectors() = viewModel.run {
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
    collectWithLifecycle(previousStop) { }
    collectWithLifecycle(predestination) { }
    collectWithLifecycle(notifyNextStop) { stop ->
      if (activity?.isFinishing != true && stop != Stop.EMPTY) {
        NextStopDialog.newInstance().show(parentFragmentManager, NextStopDialog::class.java.simpleName)
      }
    }
    collectWithLifecycle(notifyArrived) {
      parentFragmentManager.setFragmentResult(RESULT_ARRIVED, bundleOf())
      router.exit()
    }
  }

  companion object {
    fun newInstance(args: Bundle) = TrackRouteFragment().apply {
      arguments = args
    }
  }
}
