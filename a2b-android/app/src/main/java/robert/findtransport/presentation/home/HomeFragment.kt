package robert.findtransport.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.Stop
import robert.findtransport.databinding.FragmentHomeBinding
import robert.findtransport.utils.RESULT_FROM
import robert.findtransport.utils.RESULT_LOCATION_PERMISSION
import robert.findtransport.utils.RESULT_TO
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
  override val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::inflate)
  override val viewModel: HomeViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setFragmentResultListener(RESULT_FROM) { key, bundle ->
      val stopId = bundle.getInt(key)
      viewModel.setFromStop(stopId)
    }
    setFragmentResultListener(RESULT_TO) { key, bundle ->
      val stopId = bundle.getInt(key)
      viewModel.setToStop(stopId)
    }
    setFragmentResultListener(RESULT_LOCATION_PERMISSION) { _, _ ->
      viewModel.startFindNearbyLocation(true)
    }
  }

  override fun FragmentHomeBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    nsvSearchArea?.onWindowInsets { v, windowInsets ->
      val padding = getDimenInt(R.dimen.margin_xx_large)
      v.bottomPadding = (windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom * 2 + padding) * 2
    } ?: clSearchArea.onWindowInsets { v, windowInsets ->
      val padding = getDimenInt(R.dimen.margin_xx_large)
      v.bottomPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + padding
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    setHasOptionsMenu(true)
  }

  override fun FragmentHomeBinding.initViews() {
    etFrom.setOnLongClickListener { true }
    etTo.setOnLongClickListener { true }

    etFrom.isSelected = true

    btnRate.setOnClickListener { viewModel.openRate() }
    btnDismiss.setOnClickListener { viewModel.dismissRate() }
//    clFromInput.setOnClickListener { router.navigateTo(stopsPickerScreen(0)) }
//    etFrom.setOnClickListener { router.navigateTo(stopsPickerScreen(0)) }
//    btnFromList.setOnClickListener { router.navigateTo(stopsPickerScreen(0)) }
//    clToInput.setOnClickListener { router.navigateTo(stopsPickerScreen(1)) }
//    etTo.setOnClickListener { router.navigateTo(stopsPickerScreen(1)) }
//    btnToList.setOnClickListener { router.navigateTo(stopsPickerScreen(1)) }
    btnFromMap.setOnClickListener {
      lifecycleScope.launchWhenCreated {
        val stop = viewModel.toStop.value
        val coordinates = if (stop != Stop.EMPTY) {
          viewModel.getCoordinates(stop)
        } else {
          null
        }
//        router.navigateTo(mapChooserScreen(coordinates))
      }
    }
    btnToMap.setOnClickListener {
      lifecycleScope.launchWhenCreated {
        val stop = viewModel.fromStop.value
        val coordinates = if (stop != Stop.EMPTY) {
          viewModel.getCoordinates(stop)
        } else {
          null
        }
//        router.navigateTo(mapChooserScreen(coordinates))
      }
    }
    btnSearch.setOnClickListener { viewModel.search() }
    fabSwap.setOnClickListener { viewModel.swapStops() }
  }

  override fun HomeViewModel.initObservers() {
    collectWithLifecycle(allTransportsError) { showToast("ERROR") }
    collectWithLifecycle(openRate) { rate() }
//    collectWithLifecycle(openUpdate) { router.navigateTo(updateScreen()) }
    collectWithLifecycle(openSearch) { ids ->
//      router.navigateTo(
//        searchScreen(
//          bundleOf(
//            ARG_FROM_ID to ids.first,
//            ARG_TO_ID to ids.second,
//            ARG_ADD_TO_HISTORY to true
//          )
//        )
//      )
    }
    collectWithLifecycle(showRate) { show -> binding.cvRate.visibility = if (show) View.VISIBLE else View.GONE }
    collectWithLifecycle(fromStop.combineTransform(locale) { stop, locale -> emit(stop to locale) }) { stopAndLocale ->
      val stop = stopAndLocale.first
      val locale = stopAndLocale.second

      binding.etFrom.setStopName(stop, locale)
    }
    collectWithLifecycle(toStop.combineTransform(locale) { stop, locale -> emit(stop to locale) }) { stopAndLocale ->
      val stop = stopAndLocale.first
      val locale = stopAndLocale.second

      binding.etTo.setStopName(stop, locale)
    }
    collectWithLifecycle(hasLocationPermission) { locationPermission -> binding.btnFromMap.setLocationIcon(locationPermission) }
    collectWithLifecycle(fromError) { error -> binding.tvFromError.setDisappearingError(error) }
    collectWithLifecycle(toError) { error -> binding.tvToError.setDisappearingError(error) }
  }

  private fun rate() = activity?.run {
    val reviewManager = ReviewManagerFactory.create(this)
    val requestReviewFlow = reviewManager.requestReviewFlow()
    requestReviewFlow.addOnCompleteListener { request ->
      if (request.isSuccessful) {
        val reviewInfo = request.result
        val flow = reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
          if (it.isSuccessful) {
            Log.d("Rate: ", request.result.toString())
          } else {
            Log.e("Error: ", it.exception.toString())
          }
        }
      } else {
        Log.e("Error: ", request.exception.toString())
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
    inflater.inflate(R.menu.menu_main, menu)

  companion object {
    fun newInstance() = HomeFragment()
  }
}
