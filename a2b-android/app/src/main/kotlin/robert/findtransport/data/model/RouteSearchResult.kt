package robert.findtransport.data.model

data class RouteSearchResult(
  val type: RouteSearchElementType,
  val stop: Stop? = null,
  val transport: Transport? = null,
  val walkDestination: String? = null,
  val case: RouteSearchCase,
)

enum class RouteSearchElementType {
  WALK_FROM, WALK_TO, TRANSPORT_TITLE, TRANSPORT, INTERCHANGE_FROM, INTERCHANGE_TO;
}

enum class RouteSearchCase {
  SINGLE_FROM, // origin to destination nearby, then walk
  SINGLE_TO,  // walk from origin to a nearby then to destination
  FROM_TO, // origin to some point then to destination
}