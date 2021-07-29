package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.viewbinding.ViewBinding
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.Stop
import robert.findtransport.databinding.ItemRouteEndBinding
import robert.findtransport.databinding.ItemRouteMidBinding
import robert.findtransport.databinding.ItemRouteStartBinding
import robert.findtransport.presentation.component.rv.StopsDiffCallback
import robert.findtransport.presentation.detail.DetailViewModel
import robert.findtransport.utils.extensions.setStopName
import robert.findtransport.utils.extensions.setStopOptionsMenu

@Suppress("UNCHECKED_CAST")
class TransportRouteAdapter(
    private val detailViewModel: DetailViewModel,
    private val currentLocale: String
) : BaseRecyclerViewAdapter<ViewBinding, Stop, BaseViewHolder<ViewBinding, Stop>>(AsyncDifferConfig.Builder(StopsDiffCallback())) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding, Stop> =
      when (Types.getByValue(viewType)) {
        Types.START -> StartStopViewHolder(ItemRouteStartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Types.MID -> MidStopViewHolder(ItemRouteMidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Types.END -> EndStopViewHolder(ItemRouteEndBinding.inflate(LayoutInflater.from(parent.context), parent, false))
      } as BaseViewHolder<ViewBinding, Stop>

  override fun getItemViewType(position: Int): Int =
      when (position) {
        0 -> Types.START.ordinal
        itemCount - 1 -> Types.END.ordinal
        else -> Types.MID.ordinal
      }

  inner class StartStopViewHolder(private val binding: ItemRouteStartBinding) : BaseViewHolder<ItemRouteStartBinding, Stop>(binding) {
    override fun bind(item: Stop) {
      binding.run {
        stopNameStart.setStopName(item, currentLocale)
        optionsStart.setStopOptionsMenu(detailViewModel, item)
        optionsStart.visibility = if (detailViewModel.hasOptions.value) View.VISIBLE else View.GONE
      }
    }
  }
  
  inner class MidStopViewHolder(private val binding: ItemRouteMidBinding) : BaseViewHolder<ItemRouteMidBinding, Stop>(binding) {
    override fun bind(item: Stop) {
      binding.run {
        stopNameMid.setStopName(item, currentLocale)
        optionsMid.setStopOptionsMenu(detailViewModel, item)
        optionsMid.visibility = if (detailViewModel.hasOptions.value) View.VISIBLE else View.GONE
      }
    }
  }
  
  inner class EndStopViewHolder(private val binding: ItemRouteEndBinding) : BaseViewHolder<ItemRouteEndBinding, Stop>(binding) {
    override fun bind(item: Stop) {
      binding.run {
        stopNameEnd.setStopName(item, currentLocale)
        optionsEnd.setStopOptionsMenu(detailViewModel, item)
        optionsEnd.visibility = if (detailViewModel.hasOptions.value) View.VISIBLE else View.GONE
      }
    }
  }
  
  enum class Types {
    START, MID, END;
    
    companion object {
      fun getByValue(value: Int): Types =
          when (value) {
            0 -> START
            1 -> MID
            else -> END
          }
    }
  }
}
