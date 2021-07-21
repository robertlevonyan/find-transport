package robert.findtransport.presentation.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import robert.findtransport.databinding.DialogArrivedBinding
import robert.findtransport.databinding.DialogNextStopBinding
import robert.findtransport.utils.viewbinding.viewBinding

class ArrivedDialog() : DialogFragment() {
  private val binding by viewBinding(DialogArrivedBinding::inflate)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
    binding.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    dialog?.run {
      window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }

    binding.btnClose.post {
      binding.btnClose.setOnClickListener {
        dismiss()
      }
    }
  }

  companion object {
    fun newInstance() = ArrivedDialog()
  }
}
