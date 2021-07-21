package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.SettingData

class SettingsDiffCallback : DiffUtil.ItemCallback<SettingData>() {
  override fun areItemsTheSame(oldItem: SettingData, newItem: SettingData): Boolean = oldItem.type == newItem.type

  override fun areContentsTheSame(oldItem: SettingData, newItem: SettingData): Boolean = oldItem == newItem
}
