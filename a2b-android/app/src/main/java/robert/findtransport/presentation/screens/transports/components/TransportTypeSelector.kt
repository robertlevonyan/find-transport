package robert.findtransport.presentation.screens.transports.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.reusables.HalfPadding

@Composable
fun TransportTypeSelector(
  transportCategory: TransportCategory,
  onTransportCategoryClick: (TransportCategory) -> Unit,
) {
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
      onTransportCategoryClick = onTransportCategoryClick,
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
      onTransportCategoryClick = onTransportCategoryClick,
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
      onTransportCategoryClick = onTransportCategoryClick,
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
      onTransportCategoryClick = onTransportCategoryClick,
    )
  }
}
