package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.History
import robert.findtransport.databinding.ItemHistoryBinding
import robert.findtransport.presentation.component.rv.HistoryDiffCallback
import robert.findtransport.utils.extensions.setDate
import robert.findtransport.utils.extensions.setHistoryOptionsMenu
import robert.findtransport.utils.extensions.setStopName

class HistoryAdapter(
  private val currentLocale: String,
  private val onItemClickAction: (History) -> Unit,
  private val onMenuClickAction: (History) -> Unit,
) :
  BaseRecyclerViewAdapter<ItemHistoryBinding, History, HistoryAdapter.HistoryViewHolder>(
    AsyncDifferConfig.Builder(HistoryDiffCallback())
  ) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
    HistoryViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  fun removeItem(item: History): Int {
    val data = ArrayList(currentList)
    data.remove(item)
    submitList(data)
    return data.size
  }

  fun clear() {
    val data = ArrayList(currentList)
    data.removeAll(data)
    submitList(data)
  }

  inner class HistoryViewHolder(val binding: ItemHistoryBinding) :
    BaseViewHolder<ItemHistoryBinding, History>(binding) {

    override fun bind(item: History) {
      binding.run {
        tvFrom.setStopName(item.fromStop, currentLocale)
        tvTo.setStopName(item.toStop, currentLocale)
        ivOptions.setHistoryOptionsMenu { onMenuClickAction.invoke(item) }
        tvDate.setDate(item.timestamp, currentLocale)

        clHistory.setOnClickListener { onItemClickAction.invoke(item) }
      }
    }
  }
}
