package robert.findtransport.data.model.enums

enum class TransportType {
  BUS, MICROBUS, TROLLEYBUS, METRO, UNDEFINED;
  
  companion object {
    fun getByIndex(index: Int): TransportType =
      when (index) {
        0 -> BUS
        1 -> MICROBUS
        2 -> TROLLEYBUS
        3 -> METRO
        else -> UNDEFINED
      }
  }
}
