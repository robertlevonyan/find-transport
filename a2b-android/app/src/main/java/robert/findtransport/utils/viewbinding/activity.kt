package robert.findtransport.utils.viewbinding

import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBindingDelegate<T : ViewBinding>(
    private val activity: FragmentActivity,
    private val initializer: (LayoutInflater) -> T,
) : ReadOnlyProperty<FragmentActivity, T>, LifecycleObserver {
  private var binding: T? = null

  init {
    activity.lifecycle.addObserver(this)
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
  @Suppress("Unused")
  fun onCreate() {
    if (binding == null) {
      binding = initializer(activity.layoutInflater)
    }
    binding?.root?.let { activity.setContentView(it) }
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

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  @Suppress("Unused")
  fun onDestroy() {
    binding = null
    activity.lifecycle.removeObserver(this)
  }
}

inline fun <reified T : ViewBinding> FragmentActivity.viewBinding(noinline initializer: (LayoutInflater) -> T) =
    ActivityViewBindingDelegate(this, initializer)
