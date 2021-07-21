package robert.findtransport.data.model.enums

enum class LocationPermission {
  HAS_PERMISSION, NO_PERMISSION, LOADING, UNDEFINED;
  
  companion object {
    fun getByIndex(index: Int): LocationPermission = when (index) {
      0 -> HAS_PERMISSION
      1 -> NO_PERMISSION
      2 -> LOADING
      else -> UNDEFINED
    }
  }
}