package robert.findtransport.presentation.reusables

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}