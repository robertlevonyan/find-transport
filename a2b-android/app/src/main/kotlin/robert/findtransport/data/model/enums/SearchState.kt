package robert.findtransport.data.model.enums

import robert.findtransport.data.model.RouteSearchResult

sealed class SearchState {
    object NotStarted : SearchState()
    object Searching : SearchState()
    data class Result(val result: List<RouteSearchResult>) : SearchState()
    data class Failed(val reason: ExceptionType) : SearchState()
}
