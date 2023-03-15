package robert.findtransport.presentation.screens.track

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.Stop
import robert.findtransport.data.model.Transport
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.*
import robert.findtransport.presentation.reusables.composables.A2bDialog
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.utils.extensions.getCurrentName
import robert.findtransport.utils.extensions.getTypeName
import robert.findtransport.utils.extensions.showToast
import java.util.*

@Composable
fun TrackRouteContent(
  modifier: Modifier,
  navController: NavController,
  trackRouteViewModel: TrackRouteViewModel,
) {
  val locale by trackRouteViewModel.locale.collectAsState()
  val currentStop by trackRouteViewModel.currentStop.collectAsState()
  val selectedTransport by trackRouteViewModel.selectedTransport.collectAsState(initial = Transport.EMPTY)
  val arrived by trackRouteViewModel.notifyArrived.collectAsState()
  val nextStop by trackRouteViewModel.notifyNextStop.collectAsState()
  var showNextStopDialog by rememberSaveable { mutableStateOf(nextStop != Stop.EMPTY) }

  if (arrived) {
    LocalContext.current.showToast(R.string.message_arrived)
    navController.popBackStack(route = NavigationScreens.HomeScreen.name, inclusive = false)
  }

  if (showNextStopDialog) {
    A2bDialog(
      title = stringResource(id = R.string.label_arriving),
      text = stringResource(id = R.string.message_next_stop),
      image = painterResource(id = R.drawable.il_transport_arriving),
      onConfirm = { showNextStopDialog = false }) {
      showNextStopDialog = false
    }
  }

  ConstraintLayout(
    modifier = modifier
      .fillMaxSize()
      .background(color = Accent)
  ) {
    val (labelSelected, labelStopName, stopName, stopButton, progress) = createRefs()
    val guide = createGuidelineFromTop(fraction = 0.5f)

    if (selectedTransport != Transport.EMPTY) {
      val label = stringResource(id = R.string.label_tracker_transport)
      val typeNameRes = selectedTransport.getTypeName()
      val typeName =
        if (typeNameRes == -1) "" else stringResource(typeNameRes).lowercase(Locale.ROOT)
      val selectedTransportString = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
          append(label)
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
          append(" ")
          append(typeName)
          append(" ")
          append(selectedTransport.number)
        }
      }

      TextPrimary(
        modifier = Modifier
          .padding(top = dimensionResource(id = R.dimen.margin_85))
          .constrainAs(labelSelected) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            top.linkTo(parent.top)
          },
        text = selectedTransportString,
        color = Black,
      )
    }

    if (currentStop == Stop.EMPTY) {
      CircularProgressIndicator(
        modifier = modifier.constrainAs(progress) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          bottom.linkTo(parent.bottom)
          end.linkTo(parent.end)
          start.linkTo(parent.start)
          top.linkTo(parent.top)
        },
        color = Black,
      )
    } else {
      TextSecondary(
        modifier = Modifier
          .padding(bottom = HalfPadding)
          .constrainAs(labelStopName) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            bottom.linkTo(guide)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
          },
        text = stringResource(id = R.string.label_current_stop),
        color = Black,
      )

      TextPrimary(
        modifier = Modifier
          .padding(top = HalfPadding)
          .padding(horizontal = FabPadding)
          .constrainAs(stopName) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            top.linkTo(guide)
          },
        text = currentStop.getCurrentName(locale),
        fontSize = TextTrackerLabel,
        color = Black,
        textAlign = TextAlign.Center,
      )

      RegularButton(
        modifier = Modifier
          .padding(bottom = dimensionResource(id = R.dimen.margin_85))
          .constrainAs(stopButton) {
            width = Dimension.wrapContent
            height = Dimension.wrapContent
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
          },
        text = stringResource(id = R.string.label_stop_tracker),
        colors = ButtonDefaults.buttonColors(
          containerColor = Black,
          contentColor = Accent,
        )
      ) {
        navController.popBackStack()
      }
    }
  }
}
