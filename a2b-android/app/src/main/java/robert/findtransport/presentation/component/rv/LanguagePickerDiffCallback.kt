package robert.findtransport.presentation.component.rv

import androidx.recyclerview.widget.DiffUtil
import robert.findtransport.data.model.LanguageData

class LanguagePickerDiffCallback : DiffUtil.ItemCallback<LanguageData>() {
  override fun areItemsTheSame(oldItem: LanguageData, newItem: LanguageData): Boolean = oldItem.language == newItem.language

  override fun areContentsTheSame(oldItem: LanguageData, newItem: LanguageData): Boolean = oldItem == newItem
}
