package robert.findtransport.presentation.component.rv

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import robert.findtransport.R
import robert.findtransport.utils.extensions.getDimenInt

class VerticalSpaceItemDecoration : RecyclerView.ItemDecoration() {
  
  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    super.getItemOffsets(outRect, view, parent, state)
    outRect.bottom = view.context.getDimenInt(R.dimen.fab_margin)
  }
}
