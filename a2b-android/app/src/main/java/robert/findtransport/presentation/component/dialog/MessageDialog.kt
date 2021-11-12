package robert.findtransport.presentation.component.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import robert.findtransport.databinding.DialogMessageBinding
import robert.findtransport.utils.ARG_MESSAGE_DESCRIPTION
import robert.findtransport.utils.ARG_MESSAGE_TITLE

class MessageDialog(context: Context, private val arguments: Bundle) : Dialog(context) {

  private var binding: DialogMessageBinding? = null
  var onYesClick: () -> Unit = {}
  var onNoClick: () -> Unit = {}
  var message = ""
    set(value) {
      field = value
      binding?.tvDescription?.text = field
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    DialogMessageBinding.inflate(layoutInflater)
      .also { binding = it }
      .run { setContentView(root) }
    setCancelable(false)

    arguments.takeIf { it.containsKey(ARG_MESSAGE_TITLE) }
      ?.run { getInt(ARG_MESSAGE_TITLE) }
      ?.also { binding?.tvTitle?.setText(it) }

    arguments.takeIf { it.containsKey(ARG_MESSAGE_DESCRIPTION) }
      ?.run { getInt(ARG_MESSAGE_DESCRIPTION) }
      ?.also { binding?.tvDescription?.setText(it) }
      ?.let { message = context.getString(it) }

    binding?.run {
      btnYes.setOnClickListener {
        onYesClick()
        dismiss()
      }
      btnNo.setOnClickListener {
        onNoClick()
        dismiss()
      }
    }
  }

  override fun dismiss() {
    binding = null
    super.dismiss()
  }

  companion object {
    fun newInstance(context: Context, args: Bundle): MessageDialog = MessageDialog(context, args)
  }
}
