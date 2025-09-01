package robert.findtransport.presentation.screens.transports

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.enums.TransportCategory
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.composables.A2bAlertDialog
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun TransportsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    transportsViewModel: TransportsViewModel = hiltViewModel(),
) {
    val locale by transportsViewModel.locale.collectAsState()
    var transportCategory by rememberSaveable { mutableStateOf(TransportCategory.BUS) }
    var showInfo by rememberSaveable { mutableStateOf(false) }

    Scaffold(modifier = modifier, topBar = {
        A2bAppBar(
            title = stringResource(id = R.string.title_transports),
            navigationIcon = R.drawable.ic_arrow_back,
            onNavigationIconClick = { navController.popBackStack() },
            onInfoClick = { showInfo = true },
        )
    }) { contentPadding ->
        val transports by when (transportCategory) {
            TransportCategory.BUS -> transportsViewModel.buses
            TransportCategory.MICROBUS -> transportsViewModel.microbuses
            TransportCategory.TROLLEYBUS -> transportsViewModel.trolleybuses
            TransportCategory.METRO -> transportsViewModel.metro
        }.collectAsState()

        TransportsContent(
            modifier = Modifier
                .padding(contentPadding),
            transports = transports,
            locale = locale,
            transportCategory = transportCategory,
            onTransportCategoryClick = { transportCategory = it },
            onTransportClick = { transport ->
                navController.navigate(
                    route = NavigationScreens.TransportScreen(
                        transportId = transport.id,
                        showOptions = true,
                    )
                ) {
                    navController.graph.route?.let { route ->
                        popUpTo(route) { saveState = true }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
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
