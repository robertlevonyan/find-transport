package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.Transport

class TransportItemDiffCallback : DiffUtil.ItemCallback<Transport>() {
  override fun areItemsTheSame(oldItem: Transport, newItem: Transport): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Transport, newItem: Transport): Boolean = oldItem == newItem
}
