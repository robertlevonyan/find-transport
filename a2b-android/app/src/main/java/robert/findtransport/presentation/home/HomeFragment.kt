package robert.findtransport.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentHomeBinding
import robert.findtransport.di.*
import robert.findtransport.utils.*
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
    inputFrom.setOnLongClickListener { true }
    inputTo.setOnLongClickListener { true }

    inputFrom.isSelected = true

    btnRate.setOnClickListener { viewModel.openRate() }
    btnDismiss.setOnClickListener { viewModel.dismissRate() }
    clFromInput.setOnClickListener { viewModel.notifyOpenStops(0) }
    inputFrom.setOnClickListener { viewModel.notifyOpenStops(0) }
    btnFromList.setOnClickListener { viewModel.notifyOpenStops(0) }
    clToInput.setOnClickListener { viewModel.notifyOpenStops(1) }
    inputTo.setOnClickListener { viewModel.notifyOpenStops(1) }
    btnToList.setOnClickListener { viewModel.notifyOpenStops(1) }
    btnFromMap.setOnClickListener { viewModel.notifyOpenMap() }
    btnToMap.setOnClickListener { viewModel.notifyOpenMap() }
    btnSearch.setOnClickListener { viewModel.search() }
    fabSwap.setOnClickListener { viewModel.swapStops() }
    btnTransports.setOnClickListener { router.navigateTo(transportsScreen()) }
  }

  override fun HomeViewModel.initObservers() {
    collectWithLifecycle(openMap) {
      router.navigateTo(mapChooserScreen())
    }
    collectWithLifecycle(allTransportsError) { showToast("ERROR") }
    collectWithLifecycle(openStops) { type -> router.navigateTo(stopsPickerScreen(type)) }
    collectWithLifecycle(openRate) { rate() }
    collectWithLifecycle(openUpdate) { router.navigateTo(updateScreen()) }
    collectWithLifecycle(openSearch) { ids ->
      router.navigateTo(
        searchScreen(
          bundleOf(
            ARG_FROM_ID to ids.first,
            ARG_TO_ID to ids.second,
            ARG_ADD_TO_HISTORY to true
          )
        )
      )
    }
    collectWithLifecycle(showRate) { show -> binding.cvRate.visibility = if (show) View.VISIBLE else View.GONE }
    collectWithLifecycle(fromStop.combineTransform(locale) { stop, locale -> emit(stop to locale) }) { stopAndLocale ->
      val stop = stopAndLocale.first
      val locale = stopAndLocale.second

      binding.inputFrom.setStopName(stop, locale)
    }
    collectWithLifecycle(toStop.combineTransform(locale) { stop, locale -> emit(stop to locale) }) { stopAndLocale ->
      val stop = stopAndLocale.first
      val locale = stopAndLocale.second

      binding.inputTo.setStopName(stop, locale)
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

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_history -> {
        router.navigateTo(historyScreen())
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  companion object {
    fun newInstance() = HomeFragment()
  }
}
