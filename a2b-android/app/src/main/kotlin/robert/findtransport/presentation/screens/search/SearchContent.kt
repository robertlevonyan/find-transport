package robert.findtransport.presentation.screens.search

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.RouteSearchElementType
import robert.findtransport.data.model.enums.SearchState
import robert.findtransport.presentation.navigation.NavigationScreens
import robert.findtransport.presentation.screens.search.components.InterchangeFromElement
import robert.findtransport.presentation.screens.search.components.InterchangeToElement
import robert.findtransport.presentation.screens.search.components.Loading
import robert.findtransport.presentation.screens.search.components.SearchHeader
import robert.findtransport.presentation.screens.search.components.TransportElement
import robert.findtransport.presentation.screens.search.components.TransportTitleElement
import robert.findtransport.presentation.screens.search.components.WalkFromElement
import robert.findtransport.presentation.screens.search.components.WalkToElement

@Composable
fun SearchContent(
  modifier: Modifier,
  navController: NavController,
  searchViewModel: SearchViewModel,
  originName: String,
  destinationName: String,
) {
  val searchResults by searchViewModel.searchResults.collectAsState()
  val locale by searchViewModel.locale.collectAsState()
  val currentContext = LocalContext.current.applicationContext

  LazyColumn(modifier = modifier) {
    item {
      SearchHeader(
        originName = originName,
        destinationName = destinationName,
      )
    }

    when (searchResults) {
      SearchState.Searching -> item { Loading() }
      is SearchState.Result -> {
        val result = (searchResults as SearchState.Result).result
        items(result) { multiRouteElement ->
          when (multiRouteElement.type) {
            RouteSearchElementType.WALK_FROM -> WalkFromElement(multiRouteElement, locale)
            RouteSearchElementType.WALK_TO -> WalkToElement(multiRouteElement, locale)
            RouteSearchElementType.TRANSPORT_TITLE -> TransportTitleElement(
              multiRouteElement = multiRouteElement,
              locale = locale,
            )

            RouteSearchElementType.TRANSPORT -> TransportElement(
              multiRouteElement = multiRouteElement,
              locale = locale,
            ) { transport ->
              navController.navigate(
                route = NavigationScreens.TransportScreen.name + "?transport_id=${transport.id}"
                    + "&show_options=${false}"
              )
            }

            RouteSearchElementType.INTERCHANGE_FROM -> InterchangeFromElement(
              multiRouteElement = multiRouteElement,
              locale = locale,
            )

            RouteSearchElementType.INTERCHANGE_TO -> InterchangeToElement(
              multiRouteElement = multiRouteElement,
              locale = locale,
            )
          }
        }
      }

      is SearchState.Failed -> {
        Toast.makeText(currentContext, R.string.error_no_routes, Toast.LENGTH_SHORT).show()
        navController.popBackStack()
        item { Box(modifier = Modifier.size(0.dp)) }
      }

      SearchState.NotStarted -> item { Box(modifier = Modifier.size(0.dp)) }
    }
  }
}
