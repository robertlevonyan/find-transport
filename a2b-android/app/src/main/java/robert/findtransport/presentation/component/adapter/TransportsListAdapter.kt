package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.R
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.Transport
import robert.findtransport.databinding.ItemTransportBinding
import robert.findtransport.presentation.component.rv.TransportsDiffCallback
import robert.findtransport.utils.LNG_EN
import robert.findtransport.utils.extensions.setFirstLastStop
import robert.findtransport.utils.extensions.setTransportIcon
import robert.findtransport.utils.extensions.setTransportType

class TransportsListAdapter(private val onItemClick: (Transport) -> Unit = {}) : BaseRecyclerViewAdapter<
    ItemTransportBinding,
    Transport,
    TransportsListAdapter.TransportViewHolder>(AsyncDifferConfig.Builder(TransportsDiffCallback())) {
  var currentLocale: String = LNG_EN
  private var onTransportFavoriteToggle: OnTransportFavoriteToggle? = null
  private var onTransportTrackClick: OnTransportTrackClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportViewHolder =
      TransportViewHolder(ItemTransportBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class TransportViewHolder(val binding: ItemTransportBinding) :
      BaseViewHolder<ItemTransportBinding, Transport>(binding) {

    override fun bind(item: Transport) {
      binding.run {
        ivTransportIcon.setTransportIcon(item)
        tvTransportType.setTransportType(item)
        tvFirstLastStops.setFirstLastStop(item, currentLocale)
        tvTransportNumber.text = item.number
        ivFavorite.run {
          visibility = if (onTransportFavoriteToggle != null) View.VISIBLE else View.INVISIBLE
          setImageResource(if (item.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline)
          setOnClickListener { onTransportFavoriteToggle?.onToggle(item) }
        }
        ivTrack.run {
          visibility = if (onTransportTrackClick != null) View.VISIBLE else View.INVISIBLE
          setOnClickListener { onTransportTrackClick?.onTransportTrackClick(item) }
        }
      }
      itemView.setOnClickListener { onItemClick.invoke(item) }
    }
  }

  fun setOnTransportFavoriteToggle(callback: OnTransportFavoriteToggle) {
    onTransportFavoriteToggle = callback
  }

  fun setOnTransportTrackClickListener(callback: OnTransportTrackClickListener) {
    onTransportTrackClick = callback
  }

  fun interface OnTransportFavoriteToggle {
    fun onToggle(item: Transport)
  }

  fun interface OnTransportTrackClickListener {
    fun onTransportTrackClick(item: Transport)
  }
}
