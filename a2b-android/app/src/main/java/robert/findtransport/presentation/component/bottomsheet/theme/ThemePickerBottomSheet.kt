package robert.findtransport.presentation.component.bottomsheet.theme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.base.BaseBottomSheetFragment
import robert.findtransport.data.model.ThemeData
import robert.findtransport.databinding.BottomSheetThemePickerBinding
import robert.findtransport.presentation.component.adapter.ThemesAdapter
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class ThemePickerBottomSheet : BaseBottomSheetFragment<ThemePickerViewModel, BottomSheetThemePickerBinding>() {
  override val binding: BottomSheetThemePickerBinding by viewBinding(BottomSheetThemePickerBinding::inflate)
  override val viewModel: ThemePickerViewModel by viewModels()

  var onThemeSelected: (ThemeData) -> Unit = {}

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.run {
      collectWithLifecycle(themesList) { binding.rvThemes.adapter = ThemesAdapter(viewModel).apply { submitList(it) } }
      collectWithLifecycle(selectedTheme) { onThemeSelected(it).run { dismiss() } }
    }
  }
}
