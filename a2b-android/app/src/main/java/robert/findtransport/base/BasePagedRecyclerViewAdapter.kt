package robert.findtransport.base

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

abstract class BasePagedRecyclerViewAdapter<Binding : ViewBinding, Item : Any,
    ViewHolder : BaseViewHolder<Binding, Item>>(diffCallback: DiffUtil.ItemCallback<Item>) :
    PagingDataAdapter<Item, ViewHolder>(diffCallback) {

  override fun onBindViewHolder(holder: ViewHolder, position: Int) =
      getItem(position)
          ?.let { holder.bind(it) }
          ?: Unit
}
