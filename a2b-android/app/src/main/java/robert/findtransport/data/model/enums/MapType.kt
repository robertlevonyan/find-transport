package robert.findtransport.data.model.enums

enum class MapType {
  CHOOSER, PREVIEW, SEARCH;

  companion object {
    fun getByIndex(index: Int): MapType =
      when (index) {
        1 -> PREVIEW
        2 -> SEARCH
        else -> CHOOSER
      }
  }
}
