package robert.findtransport.data.model.error

sealed class DataDownloadExceptions : Exception() {
  class NoInternetException : DataDownloadExceptions()
  class NotDownloadedException : DataDownloadExceptions()
  class NotEnoughSpaceException : DataDownloadExceptions()
  class VpnException : DataDownloadExceptions()
}
