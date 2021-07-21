package robert.findtransport.base

abstract class BasePickerViewModel<in T> : BaseViewModel() {
  
  abstract fun onItemClick(data: T)
}
