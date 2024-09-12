package robert.findtransport.presentation.screens.transports.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.reusables.theme.HalfPadding

@Composable
fun TransportTypeSelector(
    transportCategory: TransportCategory,
    onTransportCategoryClick: (TransportCategory) -> Unit,
) {
    var selectedCategory by remember { mutableStateOf(TransportCategory.BUS) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HalfPadding)
    ) {
        val (busCard, microbusCard, trolleybusCard, metroCard) = createRefs()

        TransportTypeBus(
            modifier = Modifier.constrainAs(busCard) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(microbusCard.start)
            },
            transportCategory = transportCategory,
            isSelected = selectedCategory == TransportCategory.BUS,
            onTransportCategoryClick = {
                selectedCategory = it
                onTransportCategoryClick(it)
            },
        )

        TransportTypeMicrobus(
            modifier = Modifier.constrainAs(microbusCard) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(busCard.end)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            transportCategory = transportCategory,
            isSelected = selectedCategory == TransportCategory.MICROBUS,
            onTransportCategoryClick = {
                selectedCategory = it
                onTransportCategoryClick(it)
            },
        )

        TransportTypeTrolleybus(
            modifier = Modifier.constrainAs(trolleybusCard) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(parent.start)
                top.linkTo(busCard.bottom)
                end.linkTo(metroCard.start)
            },
            transportCategory = transportCategory,
            isSelected = selectedCategory == TransportCategory.TROLLEYBUS,
            onTransportCategoryClick = {
                selectedCategory = it
                onTransportCategoryClick(it)
            },
        )

        TransportTypeMetro(
            modifier = Modifier.constrainAs(metroCard) {
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
                start.linkTo(trolleybusCard.end)
                top.linkTo(microbusCard.bottom)
                end.linkTo(parent.end)
            },
            transportCategory = transportCategory,
            isSelected = selectedCategory == TransportCategory.METRO,
            onTransportCategoryClick = {
                selectedCategory = it
                onTransportCategoryClick(it)
            },
        )
    }
}
