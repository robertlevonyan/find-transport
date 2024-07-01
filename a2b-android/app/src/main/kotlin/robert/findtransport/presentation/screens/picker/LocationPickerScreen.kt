package robert.findtransport.presentation.screens.picker

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.enums.StopType
import robert.findtransport.presentation.reusables.theme.Accent
import robert.findtransport.presentation.reusables.theme.Black
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.Shapes
import robert.findtransport.presentation.reusables.composables.TextPrimary
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel

@Composable
fun LocationPickerScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  locationPickerViewModel: LocationPickerViewModel = hiltViewModel(),
  homeViewModel: HomeViewModel,
  pickerType: StopType,
) {
  val locationEnabled = locationPickerViewModel.locationEnabled.collectAsState()
  var showPermissionDialog by rememberSaveable { mutableStateOf(!locationEnabled.value) }

  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    locationPickerViewModel.setLocationEnabled(results.values.all { it })
  }

  if (showPermissionDialog) {
    PermissionDialog(
      modifier = modifier,
      onDismiss = { showPermissionDialog = false },
      onGrant = {
        launcher.launch(
          arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
          )
        )
        showPermissionDialog = false
      },
      onDecline = { showPermissionDialog = false })
  }

  LocationPickerContent(
    modifier = modifier,
    locationPickerViewModel = locationPickerViewModel,
    homeViewModel = homeViewModel,
    navController = navController,
    pickerType = pickerType,
  )
}

@Composable
fun PermissionDialog(
  modifier: Modifier,
  onDismiss: () -> Unit,
  onGrant: () -> Unit,
  onDecline: () -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .padding(horizontal = FabPadding)
        .padding(bottom = FabPadding)
        .fillMaxWidth()
        .wrapContentSize(),
      shape = Shapes.medium,
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Column(modifier) {
        TextPrimary(
          modifier = Modifier
            .padding(FabPadding)
            .align(Alignment.CenterHorizontally),
          text = stringResource(id = R.string.permission_title)
        )
        Image(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          painter = painterResource(id = R.drawable.il_location_access),
          contentDescription = stringResource(id = R.string.permission_title)
        )
        TextSecondary(
          modifier = Modifier
            .padding(FabPadding)
            .align(Alignment.CenterHorizontally),
          text = stringResource(id = R.string.permission_message),
          textAlign = TextAlign.Start,
        )
        Button(
          modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = Black,
          ), onClick = onGrant, shape = RectangleShape
        ) {
          Text(
            text = stringResource(id = R.string.permission_yes),
            fontWeight = FontWeight.Bold,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
          )
        }
        Button(
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
          onClick = onDecline,
          shape = RectangleShape,
        ) {
          Text(
            text = stringResource(id = R.string.permission_no),
            textAlign = TextAlign.Center,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
          )
        }
      }
    }
  }
}
