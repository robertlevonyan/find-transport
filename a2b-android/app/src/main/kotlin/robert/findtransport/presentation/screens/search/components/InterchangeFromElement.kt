package robert.findtransport.presentation.screens.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.RouteSearchResult
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.IconSize
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun InterchangeFromElement(multiRouteElement: RouteSearchResult, locale: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentWidth()
  ) {
    TextSecondary(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(id = R.string.label_interchange_from),
    )
    Row(
      modifier = Modifier
        .padding(HalfPadding)
        .background(
          color = MaterialTheme.colorScheme.onPrimary,
          shape = MaterialTheme.shapes.medium,
        )
        .fillMaxWidth()
    ) {
      Image(
        modifier = Modifier
          .size(IconSize)
          .align(alignment = Alignment.CenterVertically)
          .padding(start = HalfPadding),
        painter = painterResource(R.drawable.ic_stop_sign_small),
        contentDescription = stringResource(id = R.string.label_interchange_from),
      )

      TextPrimary(
        modifier = Modifier
          .align(alignment = Alignment.CenterVertically)
          .padding(HalfPadding),
        text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}
