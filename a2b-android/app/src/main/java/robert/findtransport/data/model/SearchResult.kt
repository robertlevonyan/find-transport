package robert.findtransport.data.model

sealed class SearchResult {
  data class Single(val result: List<Transport>) : SearchResult()
  data class Multi(val result: List<MultiRoute>) : SearchResult()
}