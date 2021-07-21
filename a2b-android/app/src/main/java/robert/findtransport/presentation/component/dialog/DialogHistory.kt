package robert.findtransport.presentation.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import robert.findtransport.data.model.enums.HistoryDialogType
import robert.findtransport.databinding.DialogHistoryBinding
import robert.findtransport.utils.ARG_HISTORY_DESCRIPTION
import robert.findtransport.utils.ARG_HISTORY_DIALOG_TYPE
import robert.findtransport.utils.viewbinding.viewBinding

class DialogHistory : DialogFragment() {
  private val binding: DialogHistoryBinding by viewBinding(DialogHistoryBinding::inflate)
  var onYesClick: (HistoryDialogType) -> Unit = {}
  var onNoClick: (HistoryDialogType) -> Unit = {}

  companion object {
    fun newInstance(args: Bundle): DialogHistory = DialogHistory().apply { arguments = args }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dialog?.run {
      window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }

    arguments?.takeIf { it.containsKey(ARG_HISTORY_DESCRIPTION) }
        ?.run { getInt(ARG_HISTORY_DESCRIPTION) }
        ?.also { binding.tvDescription.setText(it) }

    arguments?.takeIf { it.containsKey(ARG_HISTORY_DIALOG_TYPE) }
        ?.run { HistoryDialogType.getByIndex(getInt(ARG_HISTORY_DESCRIPTION)) }
        ?.also { type ->
          binding.run {
            btnYes.setOnClickListener { onYesClick(type).run { dismiss() } }
            btnNo.setOnClickListener { onNoClick(type).run { dismiss() } }
          }
        }
  }
}
