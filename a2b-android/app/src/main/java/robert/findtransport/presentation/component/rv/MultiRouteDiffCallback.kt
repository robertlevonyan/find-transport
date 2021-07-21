package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.MultiRoute

class MultiRouteDiffCallback : DiffUtil.ItemCallback<MultiRoute>() {
  override fun areItemsTheSame(oldItem: MultiRoute, newItem: MultiRoute): Boolean = oldItem.stop?.id == newItem.stop?.id

  override fun areContentsTheSame(oldItem: MultiRoute, newItem: MultiRoute): Boolean = oldItem == newItem
}
