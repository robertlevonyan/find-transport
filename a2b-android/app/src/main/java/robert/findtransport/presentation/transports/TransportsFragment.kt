package robert.findtransport.presentation.transports

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentTransportsBinding
import robert.findtransport.presentation.component.adapter.TransportsListAdapter
import robert.findtransport.presentation.detail.DetailFragment
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.RESULT_FAVORITE
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

class TransportsFragment : BaseFragment<TransportsViewModel, FragmentTransportsBinding>() {
  override val binding: FragmentTransportsBinding by viewBinding(FragmentTransportsBinding::inflate)
  override val viewModel: TransportsViewModel by viewModel()

  private val adapter by lazy { TransportsListAdapter(viewModel::selectTransport) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setFragmentResultListener(RESULT_FAVORITE) { _, bundle: Bundle ->
      if (bundle.getBoolean(RESULT_FAVORITE)) {
        viewModel.getTransports()
      }
    }
  }

  override fun FragmentTransportsBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    rvTransportsList.onWindowInsets { v, windowInsets ->
      val padding = getDimenInt(R.dimen.main_list_bottom_padding) + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
      v.bottomPadding = padding
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentTransportsBinding.initViews() {
    swListToggle.setOnCheckedChangeListener { _, isChecked ->
      viewModel.getTransports(isChecked)
      viewModel.setShowFavoritesToggle(isChecked)
      adapter.notifyDataSetChanged()
    }
    rvTransportsList.adapter = adapter.also { it.setOnTransportFavoriteToggle(viewModel::toggleTransportFavorite) }
  }

  override fun TransportsViewModel.initObservers() {
    observe(viewModel.allTransports) { transports ->
      adapter.currentLocale = viewModel.locale.value ?: LNG_EN
      adapter.submitList(transports)
      adapter.notifyDataSetChanged()
    }
    observe(viewModel.selectedTransport) { transport ->
      addWithSlide(DetailFragment.newInstance(transport.id, true))
    }
    observe(viewModel.showOnlyFavorites) { binding.swListToggle.isChecked = it }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = TransportsFragment()
  }
}
