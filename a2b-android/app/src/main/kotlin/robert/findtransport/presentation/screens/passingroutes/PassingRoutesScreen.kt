package robert.findtransport.presentation.screens.passingroutes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.reusables.composables.A2bAlertDialog
import robert.findtransport.presentation.reusables.composables.A2bAppBar
import robert.findtransport.utils.EMPTY_ID

@Composable
fun PassingRoutesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    stopId: Int,
    passingRoutesViewModel: PassingRoutesViewModel = hiltViewModel(),
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }
    if (stopId == EMPTY_ID) {
        navController.popBackStack()
        return
    }
    passingRoutesViewModel.getStopAndTransports(stopId)

    Scaffold(
        modifier = modifier,
        topBar = {
            A2bAppBar(
                title = stringResource(id = R.string.title_details),
                navigationIcon = R.drawable.ic_arrow_back,
                onNavigationIconClick = { navController.popBackStack() },
                onInfoClick = { showInfo = true },
            )
        }
    ) { contentPadding ->
        PassingRoutesContent(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            navController = navController,
            passingRoutesViewModel = passingRoutesViewModel,
        )
    }

    if (showInfo) {
        A2bAlertDialog(
            title = "",
            text = stringResource(R.string.message_info),
            confirmTitle = stringResource(R.string.label_ok),
            onDismissRequest = { showInfo = false },
            onConfirm = { showInfo = false },
        )
    }
}
