package robert.findtransport.presentation.component.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import robert.findtransport.databinding.DialogLocationPermissionBinding
import robert.findtransport.utils.viewbinding.viewBinding

class LocationPermissionDialog : DialogFragment() {
  private val binding: DialogLocationPermissionBinding by viewBinding(DialogLocationPermissionBinding::inflate)

  var positiveClick: () -> Unit = {}
  var negativeClick: () -> Unit = {}

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dialog?.run {
      window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      setStyle(STYLE_NO_FRAME, android.R.style.Theme)
    }
    isCancelable = false

    binding.run {
      btnYes.setOnClickListener {
        positiveClick()
        dismiss()
      }
      btnNo.setOnClickListener {
        negativeClick()
        dismiss()
      }
    }
  }
}
