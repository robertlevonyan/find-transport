package robert.findtransport.presentation.screens.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import robert.findtransport.R
import robert.findtransport.data.model.RouteSearchResult
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun TransportTitleElement(multiRouteElement: RouteSearchResult, locale: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        TextSecondary(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.label_from2)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HalfPadding)
        ) {
            Image(
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                painter = painterResource(R.drawable.ic_stop_sign_small),
                contentDescription = stringResource(id = R.string.label_from2),
            )

            TextPrimary(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(HalfPadding),
                text = multiRouteElement.stop?.getCurrentName(locale).orEmpty(),
            )
        }
        TextSecondary(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = FabPadding),
            text = stringResource(id = R.string.label_take_transport),
        )
    }
}
