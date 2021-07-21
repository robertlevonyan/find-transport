package robert.findtransport.utils.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import robert.findtransport.base.MainActivity

fun Context.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Fragment.showToast(message: String) {
  context?.showToast(message)
}

fun FragmentActivity.fullRecreate() {
  finishAndRemoveTask()
  startActivity(Intent(this, MainActivity::class.java))
}
