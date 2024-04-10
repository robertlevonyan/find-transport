package robert.findtransport.presentation.screens.data.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun OnLifecycleEvent(onEvent: (Lifecycle.Event) -> Unit) {
  val eventHandler = rememberUpdatedState(onEvent)
  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

  DisposableEffect(lifecycleOwner.value) {
    val lifecycle = lifecycleOwner.value.lifecycle
    val observer = LifecycleEventObserver { _, event ->
      eventHandler.value(event)
    }

    lifecycle.addObserver(observer)
    onDispose {
      lifecycle.removeObserver(observer)
    }
  }
}
