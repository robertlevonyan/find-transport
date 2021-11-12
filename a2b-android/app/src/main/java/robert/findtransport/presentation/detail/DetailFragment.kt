package robert.findtransport.presentation.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.enums.TransportType
import robert.findtransport.databinding.FragmentDetailBinding
import robert.findtransport.di.mapPreviewScreen
import robert.findtransport.di.passingRoutesScreen
import robert.findtransport.presentation.component.adapter.TransportRouteAdapter
import robert.findtransport.utils.*
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>() {
  override val binding: FragmentDetailBinding by viewBinding(FragmentDetailBinding::inflate)
  override val viewModel: DetailViewModel by viewModels()

  private var transportId = -1

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    arguments
      ?.takeIf { it.containsKey(ARG_TRANSPORT_ID) }
      ?.run { viewModel.getTransport(getInt(ARG_TRANSPORT_ID).also { transportId = it }) }
      ?: router.exit()
    viewModel.setHasOptions(arguments?.getBoolean(ARG_HAS_OPTIONS) == true)

  }

  override fun FragmentDetailBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    rvRoute.onWindowInsets { v, windowInsets ->
      val padding = getDimenInt(R.dimen.margin_xx_large)
      v.bottomPadding = (windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin) + padding) * 2
    }
    fabMap.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentDetailBinding.initViews() {
    fabMap.setOnClickListener { viewModel.openMapClick() }
    btnPrimaryRoute.setOnClickListener { viewModel.togglePrimary(true) }
    btnSecondaryRoute.setOnClickListener { viewModel.togglePrimary(false) }
  }

  override fun DetailViewModel.initObservers() {
    observe(fromStop) { selectedStop ->
      viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
        setFragmentResult(RESULT_FROM, bundleOf(RESULT_FROM to selectedStop.id))
        view?.showSnackbar(String.format(getString(R.string.action_set_from_route), it))
      }
    }

    observe(toStop) { selectedStop ->
      viewModel.getStopName(selectedStop).takeIf { it != "" }?.let {
        setFragmentResult(RESULT_TO, bundleOf(RESULT_TO to selectedStop.id))
        view?.showSnackbar(String.format(getString(R.string.action_set_to_route), it))
      }
    }

    observe(openMap) { open ->
      if (open) {
        router.navigateTo(
          mapPreviewScreen(
            bundleOf(
              ARG_TRANSPORT_ID to transportId,
              ARG_ROUTE_REVERSE to viewModel.showPrimary.value,
              ARG_UNDERGROUND to (selectedTransport.value.type == TransportType.METRO)
            )
          )
        )
      }
    }

    observe(openPassingTransports) { selectedStop ->
      router.navigateTo(passingRoutesScreen(selectedStop.id))
    }

    observe(selectedTransport
      .combineTransform(showPrimary) { transport, show ->
        val stops = if (show) {
          transport.stops
        } else {
          transport.stopsReversed
        }
        emit(transport to stops)
      }
      .combineTransform(locale) { transportWithStops, locale ->
        emit(Triple(transportWithStops.first, transportWithStops.second, locale))
      }) { data ->
      val transport = data.first
      val stops = data.second
      val locale = data.third

      binding.rvRoute.adapter = TransportRouteAdapter(viewModel, locale).apply { submitList(stops) }

      binding.run {
        tvTransportNumber.text = transport.number
        ivTransportIcon.setTransportIcon(transport)
        tvTransportType.setTransportType(transport)
        tvFirstLastStops.setFirstLastStop(transport, locale)
        ivFavorite.setImageResource(if (transport.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline)
        ivFavorite.setOnClickListener {
          viewModel.toggleTransportFavorite(transport) {
            setFragmentResult(RESULT_FAVORITE, bundleOf(RESULT_FAVORITE to true))
          }
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_details, menu.apply { clear() })
  }

  companion object {
    fun newInstance(id: Int, hasOption: Boolean) =
      DetailFragment().apply {
        arguments = bundleOf(
          ARG_TRANSPORT_ID to id,
          ARG_HAS_OPTIONS to hasOption
        )
      }
  }
}
