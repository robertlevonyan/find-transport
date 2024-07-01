package robert.findtransport.presentation.screens.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import robert.findtransport.R
import robert.findtransport.data.model.History
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.utils.extensions.format
import java.util.*

@Composable
fun HistoryItem(
  history: History,
  onItemClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  ConstraintLayout(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable {
        onItemClick()
      }
  ) {
    val (labelFrom, textFrom, labelTo, textTo, textDate, imageRemove) = createRefs()

    val guide = createGuidelineFromStart(0.35f)

    TextSecondary(
      modifier = Modifier
        .constrainAs(labelFrom) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(parent.start)
          top.linkTo(parent.top)
          end.linkTo(guide)
        }
        .padding(HalfPadding),
      text = stringResource(id = R.string.label_from),
      textAlign = TextAlign.Start,
    )

    TextSecondary(
      modifier = Modifier
        .constrainAs(textFrom) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(guide)
          top.linkTo(parent.top)
          end.linkTo(imageRemove.start)
        }
        .padding(HalfPadding),
      text = history.originName,
      color = MaterialTheme.colorScheme.onPrimary,
      textAlign = TextAlign.Start,
    )

    TextSecondary(
      modifier = Modifier
        .constrainAs(labelTo) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(parent.start)
          top.linkTo(textFrom.bottom)
          end.linkTo(guide)
        }
        .padding(HalfPadding),
      text = stringResource(id = R.string.label_to),
      textAlign = TextAlign.Start,
    )

    TextSecondary(
      modifier = Modifier
        .constrainAs(textTo) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(guide)
          top.linkTo(textFrom.bottom)
          end.linkTo(imageRemove.start)
        }
        .padding(HalfPadding),
      text = history.destinationName,
      color = MaterialTheme.colorScheme.onPrimary,
      textAlign = TextAlign.Start,
    )

    TextSecondary(
      modifier = Modifier
        .constrainAs(textDate) {
          width = Dimension.fillToConstraints
          height = Dimension.wrapContent
          start.linkTo(parent.start)
          top.linkTo(textTo.bottom)
          end.linkTo(parent.end)
        }
        .padding(HalfPadding),
      text = Date(history.timestamp).format(),
      textAlign = TextAlign.Start,
    )

    IconButton(
      modifier = Modifier
        .constrainAs(imageRemove) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          top.linkTo(parent.top)
          end.linkTo(parent.end)
        },
      onClick = { onDeleteClick() }) {
      Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
    }
  }
}
