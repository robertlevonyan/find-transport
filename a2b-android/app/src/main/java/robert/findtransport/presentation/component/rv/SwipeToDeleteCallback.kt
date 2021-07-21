package robert.findtransport.presentation.component.rv

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import robert.findtransport.R
import robert.findtransport.utils.extensions.getColorFromRes
import robert.findtransport.utils.extensions.getDimen
import robert.findtransport.utils.extensions.getDrawableFromRes

class SwipeToDeleteCallback(context: Context, private val onSwiped: (Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

  private val icon = context.getDrawableFromRes(R.drawable.ic_delete)
  private val radius = context.getDimen(R.dimen.small_radius)
  private val background by lazy {
    ShapeDrawable(RoundRectShape(floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null))
        .mutate()
        .apply {
          colorFilter = PorterDuffColorFilter(context.getColorFromRes(R.color.colorRemove), PorterDuff.Mode.SRC_IN)
        }
  }

  override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    onSwiped(viewHolder.layoutPosition)
  }

  override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    val iconMargin: Int = (viewHolder.itemView.height - icon!!.intrinsicHeight) / 2
    val iconTop: Int = viewHolder.itemView.top + (viewHolder.itemView.height - icon.intrinsicHeight) / 2
    val iconBottom = iconTop + icon.intrinsicHeight

    if (dX < 0) {
      val iconLeft: Int = viewHolder.itemView.right - iconMargin - icon.intrinsicWidth
      val iconRight: Int = viewHolder.itemView.right - iconMargin
      icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

      background.setBounds(
          viewHolder.itemView.left,
          viewHolder.itemView.top,
          viewHolder.itemView.right,
          viewHolder.itemView.bottom)
    } else {
      background.setBounds(0, 0, 0, 0)
    }
    background.draw(c)
    icon.draw(c)
  }
}
