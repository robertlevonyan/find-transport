package robert.findtransport.presentation.screens.stops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.launch
import robert.findtransport.data.model.StopWithAddress
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.HalfPadding
import robert.findtransport.presentation.reusables.theme.colorVariantInvertTransparent
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.home.HomeViewModel
import robert.findtransport.presentation.screens.stops.components.StopSearchInput
import robert.findtransport.presentation.screens.stops.components.TopBar
import robert.findtransport.utils.extensions.getCurrentName

@Composable
fun StopsPickerScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    stopsPickerViewModel: StopsPickerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel,
    isFrom: Boolean,
) {
    val locale by stopsPickerViewModel.locale.collectAsState()
    val stops = stopsPickerViewModel.allStops.collectAsLazyPagingItems()
    var searchBoxState by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    stopsPickerViewModel.findStops("")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                onBackClick = { navController.popBackStack() },
                onFeedbackClick = { navController.navigate(NavigationScreens.FeedbackScreen) },
                searchBoxStateToggle = {
                    searchBoxState = !searchBoxState
                    if (!searchBoxState) {
                        stopsPickerViewModel.findStops("")
                    }
                },
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            AnimatedVisibility(visible = searchBoxState) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = FabPadding)
                ) { StopSearchInput(stopsPickerViewModel::findStops) }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = stops.itemCount,
                    key = stops.itemKey { it.id },
                    contentType = stops.itemContentType { it.nameEn },
                ) { index ->
                    val stop = stops[index]

                    if (stop == null) {
                        Box(modifier = Modifier.size(0.dp)) {}
                    } else {
                        Column {
                            TextSecondary(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isFrom) {
                                            scope.launch {
                                                val address = stopsPickerViewModel.getAddress(stop)
                                                homeViewModel.setOriginStop(
                                                    StopWithAddress(
                                                        stop,
                                                        address
                                                    )
                                                )
                                            }
                                        } else {
                                            scope.launch {
                                                val address = stopsPickerViewModel.getAddress(stop)
                                                homeViewModel.setDestinationStop(
                                                    StopWithAddress(
                                                        stop,
                                                        address
                                                    )
                                                )
                                            }
                                        }
                                        navController.popBackStack()
                                    }
                                    .padding(HalfPadding),
                                text = stop.getCurrentName(locale),
                                textAlign = TextAlign.Start,
                            )

                            Divider(
                                modifier.padding(start = FabPadding),
                                color = colorVariantInvertTransparent(),
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}
