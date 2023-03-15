package robert.findtransport.presentation.screens.history.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import robert.findtransport.R

@Composable
fun NoHistoryScreen(modifier: Modifier) {
  Box(modifier = modifier) {
    Image(
      modifier = Modifier
        .wrapContentSize()
        .align(Alignment.Center),
      painter = painterResource(id = R.drawable.il_no_data),
      contentDescription = null,
    )
  }
}
