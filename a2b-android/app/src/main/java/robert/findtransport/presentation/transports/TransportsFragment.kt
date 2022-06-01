package robert.findtransport.presentation.transports

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentTransportsBinding
import robert.findtransport.di.detailsScreen
import robert.findtransport.presentation.component.adapter.TransportsListAdapter
import robert.findtransport.presentation.compose.screens.transports.TransportsViewModel
import robert.findtransport.utils.RESULT_FAVORITE
import robert.findtransport.utils.extensions.bottomPadding
import robert.findtransport.utils.extensions.getDimenInt
import robert.findtransport.utils.extensions.onWindowInsets
import robert.findtransport.utils.extensions.topMargin
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class TransportsFragment : BaseFragment<TransportsViewModel, FragmentTransportsBinding>() {
  override val binding: FragmentTransportsBinding by viewBinding(FragmentTransportsBinding::inflate)
  override val viewModel: TransportsViewModel by viewModels()

  private val adapter by lazy {
    TransportsListAdapter { transport ->
      router.navigateTo(detailsScreen(transport.id, true))
    }
  }

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
    btnAll.setOnClickListener {
      viewModel.getTransports(false)
      viewModel.setShowFavoritesToggle(false)
    }
    btnFavorites.setOnClickListener {
      viewModel.getTransports(true)
      viewModel.setShowFavoritesToggle(true)
    }
    rvTransportsList.adapter = adapter.also { it.setOnTransportFavoriteToggle(viewModel::toggleTransportFavorite) }
  }

  override fun TransportsViewModel.initObservers() {
    collectWithLifecycle(allTransports.combineTransform(locale) { transports, locale -> emit(transports to locale) }) { data ->
      adapter.currentLocale = data.second
      adapter.submitList(data.first)
    }
    collectWithLifecycle(showOnlyFavorites) { onlyFavorites ->
      val checkedButtonId = if (onlyFavorites) R.id.btn_favorites else R.id.btn_all
      binding.swListToggle.check(checkedButtonId)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = TransportsFragment()
  }
}
