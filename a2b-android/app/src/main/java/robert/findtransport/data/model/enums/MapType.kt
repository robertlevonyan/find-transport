package robert.findtransport.data.model.enums

enum class MapType {
  CHOOSER, SEARCH;

  companion object {
    fun getByIndex(index: Int): MapType = when (index) {
      1 -> SEARCH
      else -> CHOOSER
    }
  }
}
