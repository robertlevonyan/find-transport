package robert.findtransport.data.model.enums

enum class OpenStopType {
  FROM, TO, UNDEFINED;
  companion object {
    fun getByIndex(index: Int): OpenStopType =
        when (index) {
          0 -> FROM
          1 -> TO
          else -> UNDEFINED
        }
  }
}