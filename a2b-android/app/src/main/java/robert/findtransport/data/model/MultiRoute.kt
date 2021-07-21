package robert.findtransport.data.model

data class MultiRoute(
  val type: MultiType,
  val stop: Stop? = null,
  val transport: Transport? = null,
  val case: MultiRouteCase,
)

enum class MultiType {
  WALK_FROM, WALK_TO, TRANSPORT_TITLE, TRANSPORT, INTERCHANGE_FROM, INTERCHANGE_TO;

  companion object {
    fun getByIndex(index: Int) = when (index) {
      0 -> WALK_FROM
      1 -> WALK_TO
      2 -> TRANSPORT_TITLE
      3 -> TRANSPORT
      4 -> INTERCHANGE_FROM
      else -> INTERCHANGE_TO
    }
  }
}

enum class MultiRouteCase {
  SINGLE_FROM, // origin to destination nearby, then walk
  SINGLE_TO,  // walk from origin to a nearby then to destination
  FROM_TO, // origin to some point then to destination
}