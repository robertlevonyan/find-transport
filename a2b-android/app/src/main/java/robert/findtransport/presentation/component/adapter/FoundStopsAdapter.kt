package robert.findtransport.presentation.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.Stop
import robert.findtransport.databinding.ItemSpinnerBinding
import robert.findtransport.presentation.component.rv.StopsDiffCallback
import robert.findtransport.presentation.stop.StopsPickerViewModel
import robert.findtransport.utils.extensions.setStopName

class FoundStopsAdapter(private val stopsPickerViewModel: StopsPickerViewModel) :
    BaseRecyclerViewAdapter<ItemSpinnerBinding, Stop, FoundStopsAdapter.StopsViewHolder>(
        AsyncDifferConfig.Builder(StopsDiffCallback())) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopsViewHolder =
      StopsViewHolder(ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class StopsViewHolder(private val binding: ItemSpinnerBinding) :
      BaseViewHolder<ItemSpinnerBinding, Stop>(binding) {
    override fun bind(item: Stop) {
      binding.run {
        val locale = stopsPickerViewModel.locale.value ?: return@run
        tvItem.setStopName(item, locale)
        tvItem.setOnClickListener { stopsPickerViewModel.onStopClicked(item) }
      }
    }
  }
}
