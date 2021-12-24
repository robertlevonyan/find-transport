package robert.findtransport.presentation.component.bottomsheet.language

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.base.BaseBottomSheetFragment
import robert.findtransport.data.model.LanguageData
import robert.findtransport.databinding.BottomSheetLanguagePickerBinding
import robert.findtransport.presentation.component.adapter.LanguagesAdapter
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class LanguagePickerBottomSheet : BaseBottomSheetFragment<LanguagePickerViewModel, BottomSheetLanguagePickerBinding>() {
  var onLanguageSelected: (LanguageData) -> Unit = {}

  override val binding: BottomSheetLanguagePickerBinding by viewBinding(BottomSheetLanguagePickerBinding::inflate)
  override val viewModel: LanguagePickerViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.run {
      collectWithLifecycle(languagesList) { binding.rvLanguages.adapter = LanguagesAdapter(viewModel).apply { submitList(it) } }
      collectWithLifecycle(selectedLanguage) { onLanguageSelected(it).run { dismiss() } }
    }
  }
}
