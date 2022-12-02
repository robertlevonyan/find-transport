package robert.findtransport.data.model

data class Stop(
  val id: Int = 0,
  val nameAm: String = "",
  val nameRu: String = "",
  val nameEn: String = "",
  val coordinates: List<StopLocation> = emptyList(),
) {
  companion object {
    val EMPTY = Stop()
  }
}

fun Stop.isEmpty(): Boolean = this == Stop.EMPTY
