package robert.findtransport.presentation.screens.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import robert.findtransport.R
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.Shapes
import robert.findtransport.presentation.reusables.composables.TextSecondary

@Composable
fun SearchHeader(originName: String, destinationName: String) {
  Card(
    modifier = Modifier
      .padding(horizontal = FabPadding)
      .padding(bottom = FabPadding)
      .fillMaxWidth()
      .wrapContentSize(),
    shape = Shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    ConstraintLayout(modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable {}
      .padding(vertical = HalfPadding)) {
      val (fromIcon, fromStop, toIcon, toStop) = createRefs()
      val guide = createGuidelineFromStart(0.15f)

      Image(
        modifier = Modifier.constrainAs(fromIcon) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(fromStop.top)
          end.linkTo(guide)
          bottom.linkTo(fromStop.bottom)
          start.linkTo(parent.start)
        }, painter = painterResource(id = R.drawable.ic_start_point), contentDescription = null
      )

      TextSecondary(
        modifier = Modifier.constrainAs(fromStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(parent.top)
          end.linkTo(parent.end)
          start.linkTo(guide)
        },
        text = originName,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Start,
      )

      Image(
        modifier = Modifier.constrainAs(toIcon) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(toStop.top)
          end.linkTo(guide)
          bottom.linkTo(toStop.bottom)
          start.linkTo(parent.start)
        }, painter = painterResource(id = R.drawable.ic_end_point), contentDescription = null
      )

      TextSecondary(
        modifier = Modifier.constrainAs(toStop) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          top.linkTo(fromStop.bottom)
          end.linkTo(parent.end)
          start.linkTo(guide)
          bottom.linkTo(parent.bottom)
        },
        text = destinationName,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Start,
      )
    }
  }
}
