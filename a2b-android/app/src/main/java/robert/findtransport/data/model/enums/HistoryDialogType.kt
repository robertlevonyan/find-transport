package robert.findtransport.data.model.enums

enum class HistoryDialogType {
  CLEAR, REMOVE, RESTORE, UNDEFINED;

  companion object {
    fun getByIndex(index: Int): HistoryDialogType = when (index) {
      0 -> CLEAR
      1 -> REMOVE
      2 -> RESTORE
      else -> UNDEFINED
    }
  }
}
