package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.History

class HistoryDiffCallback : DiffUtil.ItemCallback<History>() {
  override fun areItemsTheSame(oldItem: History, newItem: History): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: History, newItem: History): Boolean = oldItem == newItem
}
