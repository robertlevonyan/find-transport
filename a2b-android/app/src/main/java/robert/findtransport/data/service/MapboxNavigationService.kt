package robert.findtransport.data.service

import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.matching.v5.MapboxMapMatching
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.coordinates
import robert.findtransport.BuildConfig
import robert.findtransport.utils.extensions.asPairs

class MapboxNavigationService {

  fun getNavigation(coordinates: MutableList<Point>): MapboxMapMatching =
      MapboxMapMatching.builder()
          .accessToken(BuildConfig.MAPBOX_TOKEN)
          .coordinates(coordinates)
          .steps(true)
          .profile(DirectionsCriteria.PROFILE_DRIVING)
          .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
          .overview(DirectionsCriteria.OVERVIEW_FALSE)
          .post()
          .build()

  fun getDirections(coordinates: MutableList<Point>): List<MapboxDirections> =
      coordinates.asPairs().map(::createDirection)
//    val directions = mutableListOf<MapboxDirections>()
//    val coordinatePairs = mutableListOf<Pair<Point, Point?>>()
//
//    for (i in 0..coordinates.lastIndex step 2) {
//      val firstCoordinate = coordinates[i]
//      val nextCoordinate: Point? = if (i == coordinates.lastIndex) null else coordinates[i + 1]
//      coordinatePairs.add(firstCoordinate to nextCoordinate)
//    }
//    MapboxDirections.builder()
//        .origin(coordinates.first())
//        .destination(coordinates.last())
//        .overview(DirectionsCriteria.OVERVIEW_FULL)
//        .profile(DirectionsCriteria.PROFILE_DRIVING)
//        .accessToken(BuildConfig.MAPBOX_TOKEN)
//        .post()
//        .build()
//
//    return directions
//  }

  private fun createDirection(pair: Pair<Point, Point?>) = MapboxDirections.builder()
      .routeOptions(
          RouteOptions.builder()
              .accessToken(BuildConfig.MAPBOX_TOKEN)
              .coordinates(
                  origin = pair.first,
                  waypoints = null,
                  destination = pair.second ?: pair.first
              )
              .overview(DirectionsCriteria.OVERVIEW_SIMPLIFIED)
              .profile(DirectionsCriteria.PROFILE_DRIVING)
              .build()
      )
      .usePostMethod(true)
//      .origin(pair.first)
//      .destination(pair.second ?: pair.first)
//      .overview(DirectionsCriteria.OVERVIEW_FULL)
//      .profile(DirectionsCriteria.PROFILE_DRIVING)
//      .accessToken(BuildConfig.MAPBOX_TOKEN)
//      .post()
      .build()
}
