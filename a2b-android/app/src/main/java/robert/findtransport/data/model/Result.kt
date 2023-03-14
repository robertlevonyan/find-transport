package robert.findtransport.data.model

import robert.findtransport.data.model.error.A2bException

sealed class Result<out R> {
  data class Success<out R>(val data: R) : Result<R>()
  data class Error(val exception: A2bException) : Result<Nothing>()
}
