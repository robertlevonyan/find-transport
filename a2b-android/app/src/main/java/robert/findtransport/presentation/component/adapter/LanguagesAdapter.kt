package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.LanguageData
import robert.findtransport.databinding.ItemBottomSheetLanguageBinding
import robert.findtransport.presentation.component.bottomsheet.language.LanguagePickerViewModel
import robert.findtransport.presentation.component.rv.LanguagePickerDiffCallback
import robert.findtransport.utils.extensions.setBold

class LanguagesAdapter(private val languagePickerViewModel: LanguagePickerViewModel) :
    BaseRecyclerViewAdapter<ItemBottomSheetLanguageBinding, LanguageData, LanguagesAdapter.LanguagesViewHolder>(
        AsyncDifferConfig.Builder(LanguagePickerDiffCallback())) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesViewHolder =
      LanguagesViewHolder(ItemBottomSheetLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class LanguagesViewHolder(private val binding: ItemBottomSheetLanguageBinding) :
      BaseViewHolder<ItemBottomSheetLanguageBinding, LanguageData>(binding) {

    override fun bind(item: LanguageData) {
      binding.run {
        tvItem.text = item.language
        tvItem.setBold(item.current)
        tvItem.setOnClickListener { languagePickerViewModel.onItemClick(item) }
      }
    }
  }
}
