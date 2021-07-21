package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.Stop

class StopItemDiffCallback : DiffUtil.ItemCallback<Stop>() {
  override fun areItemsTheSame(oldItem: Stop, newItem: Stop): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Stop, newItem: Stop): Boolean = oldItem == newItem
}
