package robert.findtransport.presentation.screens.picker.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import robert.findtransport.R

@Composable
fun BoxScope.CentralPointer(isMapMoving: State<Boolean>) {
  val icon = if (isMapMoving.value) {
    R.drawable.ic_origin
  } else {
    R.drawable.ic_origin_idle
  }
  Image(
    modifier = Modifier
      .wrapContentSize()
      .align(Alignment.Center)
      .animateContentSize(),
    painter = painterResource(id = icon),
    contentDescription = null,
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
  )
}
