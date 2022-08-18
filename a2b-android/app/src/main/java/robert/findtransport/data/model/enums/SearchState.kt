package robert.findtransport.data.model.enums

import robert.findtransport.data.model.MultiRoute
import robert.findtransport.data.model.Transport

sealed class SearchState {
  object NotStarted : SearchState()
  object Searching : SearchState()
  data class Single(val result: List<Transport>) : SearchState()
  data class Multi(val result: List<MultiRoute>) : SearchState()
  data class Failed(val reason: ExceptionType) : SearchState()
}
