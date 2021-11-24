package robert.findtransport.utils.viewbinding

import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBindingDelegate<T : ViewBinding>(
  private val activity: FragmentActivity,
  private val initializer: (LayoutInflater) -> T,
) : ReadOnlyProperty<FragmentActivity, T>, LifecycleObserver {
  private var binding: T? = null

  init {
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (binding == null) {
          binding = initializer(activity.layoutInflater)
        }
        binding?.root?.let { activity.setContentView(it) }
      }

      override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        binding = null
        activity.lifecycle.removeObserver(this)
      }
    })
  }

  override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
    if (binding == null) {
      // This must be on the main thread only
      if (Looper.myLooper() != Looper.getMainLooper()) {
        throw IllegalThreadStateException("This cannot be called from other threads. It should be on the main thread only.")
      }

      binding = initializer(thisRef.layoutInflater)
    }
    return binding!!
  }
}

inline fun <reified T : ViewBinding> FragmentActivity.viewBinding(noinline initializer: (LayoutInflater) -> T) =
  ActivityViewBindingDelegate(this, initializer)
