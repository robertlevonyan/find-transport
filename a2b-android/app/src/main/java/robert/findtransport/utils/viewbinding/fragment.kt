package robert.findtransport.utils.viewbinding

import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val initializer: (LayoutInflater) -> T,
) : ReadOnlyProperty<Fragment, T> {
  private var binding: T? = null

  init {
    fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
          viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              binding = null
            }
          })
        }
      }
    })
  }

  @Suppress("UNCHECKED_CAST")
  override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
    if (binding == null) {
      if (Looper.myLooper() != Looper.getMainLooper()) {
        throw IllegalThreadStateException("This cannot be called from other threads. It should be on the main thread only.")
      }

      @Suppress("ControlFlowWithEmptyBody")
      while (!thisRef.isRemoving && !thisRef.isAdded) {
      }

      binding = initializer(thisRef.layoutInflater)
    }
    return binding!!
  }
}

inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline initializer: (LayoutInflater) -> T) =
    FragmentViewBindingDelegate(this, initializer)
