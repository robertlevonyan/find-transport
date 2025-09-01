package robert.findtransport.presentation.screens.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    originName: String,
    originLatitude: Float,
    originLongitude: Float,
    originStopId: Int,
    destinationName: String,
    destinationLatitude: Float,
    destinationLongitude: Float,
    destinationStopId: Int,
    opened: String,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = null) {
        searchViewModel.performSearch(
            originStopId = originStopId,
            destinationStopId = destinationStopId,
            originLatitude = originLatitude,
            originLongitude = originLongitude,
            destinationLatitude = destinationLatitude,
            destinationLongitude = destinationLongitude,
            opened = opened,
            originName = originName,
            destinationName = destinationName,
        )
    }

    Scaffold(modifier = modifier, topBar = {
        A2bAppBar(
            title = stringResource(id = R.string.title_search),
            navigationIcon = R.drawable.ic_arrow_back,
            onNavigationIconClick = { navController.popBackStack() },
            onInfoClick = { showInfo = true },
        )
    }) { contentPadding ->
        SearchContent(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            navController = navController,
            searchViewModel = searchViewModel,
            originName = originName,
            destinationName = destinationName,
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
