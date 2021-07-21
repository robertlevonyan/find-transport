package robert.findtransport.presentation.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.setFragmentResultListener
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentHomeBinding
import robert.findtransport.presentation.history.HistoryFragment
import robert.findtransport.presentation.map.ChooserMapFragment
import robert.findtransport.presentation.search.SearchFragment
import robert.findtransport.presentation.stop.StopsPickerFragment
import robert.findtransport.presentation.transports.TransportsFragment
import robert.findtransport.presentation.update.UpdateFragment
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding
import timber.log.Timber

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
  override val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::inflate)
  override val viewModel: HomeViewModel by viewModel()

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
    clSearchArea.onWindowInsets { v, windowInsets ->
      val padding = getDimenInt(R.dimen.margin_xx_large)
      v.bottomMargin = (windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom * 2 + padding) * 2
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    setHasOptionsMenu(true)
  }

  override fun FragmentHomeBinding.initViews() {
    inputFrom.setOnLongClickListener { true }
    inputTo.setOnLongClickListener { true }

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
    btnTransports.setOnClickListener { add(TransportsFragment.newInstance()) }
  }

  override fun HomeViewModel.initObservers() {
    observe(openMap) { addWithSlide(ChooserMapFragment.newInstance()) }
    observe(allTransportsError) { showToast("ERROR") }
    observe(openStops) { type -> addWithSlide(StopsPickerFragment.newInstance(type)) }
    observe(openRate) { rate() }
    observe(openUpdate) { replaceWithSlide(UpdateFragment.newInstance()) }
    observe(openSearch) { ids ->
      val fragment = SearchFragment.newInstance(
        bundleOf(
          ARG_FROM_ID to ids.first,
          ARG_TO_ID to ids.second,
          ARG_ADD_TO_HISTORY to true
        )
      )
      addWithSlide(fragment)
    }
    observe(showRate) { show -> binding.cvRate.visibility = if (show) View.VISIBLE else View.GONE }
    observe(fromStop) { stop ->
      val locale = viewModel.locale.value ?: return@observe
      binding.inputFrom.setStopName(stop, locale)
    }
    observe(toStop) { stop ->
      val locale = viewModel.locale.value ?: return@observe
      binding.inputTo.setStopName(stop, locale)
    }
    observe(hasLocationPermission) { locationPermission -> binding.btnFromMap.setLocationIcon(locationPermission) }
    observe(fromError) { error -> binding.tvFromError.setDisappearingError(error) }
    observe(toError) { error -> binding.tvToError.setDisappearingError(error) }
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
            Timber.tag("Rate: ").d(request.result.toString())
          } else {
            Timber.tag("Error: ").d(it.exception.toString())
          }
        }
      } else {
        Timber.tag("Error: ").d(request.exception.toString())
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
    inflater.inflate(R.menu.menu_main, menu)

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_history -> {
        addWithSlide(HistoryFragment.newInstance())
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  companion object {
    fun newInstance() = HomeFragment()
  }
}
