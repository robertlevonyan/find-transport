package robert.findtransport.presentation.screens.feedback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.presentation.reusables.composables.A2bAppBar

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    feedbackViewModel: FeedbackViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        topBar = {
            A2bAppBar(
                title = stringResource(id = R.string.title_feedback),
                hasFeedbackButton = false,
                navigationIcon = R.drawable.ic_arrow_back,
                onNavigationIconClick = { navController.popBackStack() },
                onFeedbackClick = { },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
    ) { contentPadding ->
        FeedbackContent(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            feedbackViewModel = feedbackViewModel,
            snackbarHostState = snackbarHostState,
        )
    }
}
