package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.ThemeData

class ThemePickerDiffCallback : DiffUtil.ItemCallback<ThemeData>() {
  override fun areItemsTheSame(oldItem: ThemeData, newItem: ThemeData): Boolean = oldItem.theme == newItem.theme

  override fun areContentsTheSame(oldItem: ThemeData, newItem: ThemeData): Boolean = oldItem == newItem
}
