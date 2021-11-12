package robert.findtransport.presentation.component.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import robert.findtransport.base.BasePagedRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.Stop
import robert.findtransport.databinding.ItemStopBinding
import robert.findtransport.presentation.component.rv.StopItemDiffCallback
import robert.findtransport.utils.extensions.setStopName


class StopsAdapter(
  private val locale: String,
  private val onItemClick: (Stop) -> Unit,
) : BasePagedRecyclerViewAdapter<ItemStopBinding, Stop, StopsAdapter.StopsViewHolder>(StopItemDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopsViewHolder =
    StopsViewHolder(ItemStopBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class StopsViewHolder(private val binding: ItemStopBinding) :
    BaseViewHolder<ItemStopBinding, Stop>(binding) {
    @SuppressLint("ClickableViewAccessibility")
    override fun bind(item: Stop) {
      binding.run {
        tvItem.setStopName(item, locale)
        tvItem.setOnClickListener { onItemClick.invoke(item) }
        tvItem.setOnLongClickListener { view ->
          Toast.makeText(view.context, tvItem.text?.toString() ?: "", Toast.LENGTH_SHORT).show()
          true
        }
      }
    }
  }
}
