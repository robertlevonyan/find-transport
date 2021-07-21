package robert.findtransport.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<Binding : ViewBinding, Item>(binding: Binding) :
    RecyclerView.ViewHolder(binding.root) {

  abstract fun bind(item: Item)
}
