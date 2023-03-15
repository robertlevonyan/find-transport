package robert.findtransport.presentation.screens.search.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.RouteSearchResult
import robert.findtransport.presentation.reusables.HalfPadding
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun WalkFromElement(multiRouteElement: RouteSearchResult, locale: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentWidth()
  ) {
    TextSecondary(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(id = R.string.label_walk_from),
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(HalfPadding)
    ) {
      Icon(
        modifier = Modifier.align(alignment = Alignment.CenterVertically),
        painter = painterResource(R.drawable.ic_walk),
        contentDescription = stringResource(id = R.string.label_walk_from),
      )
      TextPrimary(
        modifier = Modifier.align(alignment = Alignment.CenterVertically),
        text = multiRouteElement.stop?.getCurrentName(locale)
          ?: multiRouteElement.walkDestination.orEmpty(),
      )
    }
  }
}
