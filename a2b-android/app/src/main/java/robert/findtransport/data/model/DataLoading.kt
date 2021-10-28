package robert.findtransport.data.model

sealed class DataLoading {
  object NotStarted: DataLoading()
  object Loading: DataLoading()
  object Loaded: DataLoading()
  data class Failed(val reason: Throwable): DataLoading()
}
