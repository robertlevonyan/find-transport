package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.ThemeData
import robert.findtransport.databinding.ItemBottomSheetThemeBinding
import robert.findtransport.presentation.component.bottomsheet.theme.ThemePickerViewModel
import robert.findtransport.presentation.component.rv.ThemePickerDiffCallback
import robert.findtransport.utils.extensions.setBold

class ThemesAdapter(private val themePickerViewModel: ThemePickerViewModel) :
    BaseRecyclerViewAdapter<ItemBottomSheetThemeBinding, ThemeData, ThemesAdapter.ThemesViewHolder>(
        AsyncDifferConfig.Builder(ThemePickerDiffCallback())) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder =
      ThemesViewHolder(ItemBottomSheetThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class ThemesViewHolder(private val binding: ItemBottomSheetThemeBinding) :
      BaseViewHolder<ItemBottomSheetThemeBinding, ThemeData>(binding) {

    override fun bind(item: ThemeData) {
      binding.run {
        tvItem.setText(item.themeName)
        tvItem.setBold(item.current)
        tvItem.setOnClickListener { themePickerViewModel.onItemClick(item) }
      }
    }
  }
}
