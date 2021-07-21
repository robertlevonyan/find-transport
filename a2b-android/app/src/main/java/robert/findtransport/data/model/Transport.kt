package robert.findtransport.data.model

import robert.findtransport.data.model.enums.TransportType

data class Transport(
    var id: Int = 0,
    var number: String = "",
    var type: TransportType = TransportType.UNDEFINED,
    var isNew: Boolean = false,
    var stops: List<Stop> = emptyList(),
    var stopsReversed: List<Stop> = emptyList(),
    var isFavorite: Boolean = false,
)