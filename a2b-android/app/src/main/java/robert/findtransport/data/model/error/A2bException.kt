package robert.findtransport.data.model.error

import robert.findtransport.data.model.enums.ExceptionType

data class A2bException(val type: ExceptionType, val errorMessage: Int, val error: Exception) : Exception("")