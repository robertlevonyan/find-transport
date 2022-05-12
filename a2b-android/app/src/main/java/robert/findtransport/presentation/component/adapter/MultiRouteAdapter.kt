package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.viewbinding.ViewBinding
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.MultiRoute
import robert.findtransport.data.model.MultiType
import robert.findtransport.data.model.Transport
import robert.findtransport.databinding.*
import robert.findtransport.presentation.component.rv.MultiRouteDiffCallback
import robert.findtransport.presentation.search.SearchViewModel
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.extensions.*

class MultiRouteAdapter(
  private val onItemClick: (Transport) -> Unit,
) : BaseRecyclerViewAdapter<ViewBinding, MultiRoute, BaseViewHolder<ViewBinding, MultiRoute>>(
        AsyncDifferConfig.Builder(MultiRouteDiffCallback())) {

  var currentLocale: String = LNG_EN
  private var onTransportTrackClick: TransportsListAdapter.OnTransportTrackClickListener? = null

  @Suppress("UNCHECKED_CAST")
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding, MultiRoute> =
      when (MultiType.getByIndex(viewType)) {
        MultiType.WALK_FROM -> WalkFromViewHolder(ItemMultiRouteWalkFromBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        MultiType.WALK_TO -> WalkToViewHolder(ItemMultiRouteWalkToBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        MultiType.TRANSPORT_TITLE -> TransportTitleViewHolder(ItemMultiRouteTransportTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        MultiType.TRANSPORT -> TransportViewHolder(ItemMultiRouteTransportBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        MultiType.INTERCHANGE_FROM -> InterchangeFromViewHolder(ItemMultiRouteInterchangeFromBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        MultiType.INTERCHANGE_TO -> InterchangeToViewHolder(ItemMultiRouteInterchangeToBinding.inflate(LayoutInflater.from(parent.context), parent, false))
      } as BaseViewHolder<ViewBinding, MultiRoute>

  override fun getItemViewType(position: Int): Int {
    return getItem(position).type.ordinal
  }

  inner class WalkFromViewHolder(val binding: ItemMultiRouteWalkFromBinding) :
      BaseViewHolder<ItemMultiRouteWalkFromBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.run {
        tvStopName.setStopName(item.stop ?: return@run, currentLocale)
      }
    }
  }

  inner class WalkToViewHolder(val binding: ItemMultiRouteWalkToBinding) :
      BaseViewHolder<ItemMultiRouteWalkToBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.tvStopName.setStopName(item.stop ?: return, currentLocale)
    }
  }

  inner class TransportTitleViewHolder(val binding: ItemMultiRouteTransportTitleBinding) :
      BaseViewHolder<ItemMultiRouteTransportTitleBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.tvStopName.setStopName(item.stop ?: return, currentLocale)
    }
  }

  inner class TransportViewHolder(val binding: ItemMultiRouteTransportBinding) :
      BaseViewHolder<ItemMultiRouteTransportBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.run {
        val transport = item.transport ?: return@run
        ivTransportIcon.setTransportIcon(transport)
        tvTransportType.setTransportType(transport)
        tvFirstLastStops.setFirstLastStop(transport, currentLocale)
        tvTransportNumber.text = transport.number
        ivTrack.setOnClickListener { onTransportTrackClick?.onTransportTrackClick(transport) }
        clRoot.setOnClickListener { onItemClick.invoke(transport) }
      }
    }
  }

  inner class InterchangeFromViewHolder(val binding: ItemMultiRouteInterchangeFromBinding) :
      BaseViewHolder<ItemMultiRouteInterchangeFromBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.tvStopName.setStopName(item.stop ?: return, currentLocale)
    }
  }

  inner class InterchangeToViewHolder(val binding: ItemMultiRouteInterchangeToBinding) :
      BaseViewHolder<ItemMultiRouteInterchangeToBinding, MultiRoute>(binding) {
    override fun bind(item: MultiRoute) {
      binding.tvStopName.setStopName(item.stop ?: return, currentLocale)
    }
  }

  fun setOnTransportTrackClickListener(callback: TransportsListAdapter.OnTransportTrackClickListener) {
    onTransportTrackClick = callback
  }
}
