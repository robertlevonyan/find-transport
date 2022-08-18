package robert.findtransport.presentation.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.data.model.History
import robert.findtransport.data.model.enums.HistoryDialogType
import robert.findtransport.databinding.FragmentHistoryBinding
import robert.findtransport.presentation.component.dialog.ArrivedDialog
import robert.findtransport.presentation.component.dialog.DialogHistory
import robert.findtransport.presentation.component.rv.SwipeToDeleteCallback
import robert.findtransport.presentation.compose.screens.history.HistoryViewModel
import robert.findtransport.utils.ARG_HISTORY_DESCRIPTION
import robert.findtransport.utils.ARG_HISTORY_DIALOG_TYPE
import robert.findtransport.utils.RESULT_ARRIVED
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {
  override val binding: FragmentHistoryBinding by viewBinding(FragmentHistoryBinding::inflate)
  override val viewModel: HistoryViewModel by viewModels()

  override fun FragmentHistoryBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    rvHistory.onWindowInsets { v, windowInsets ->
      v.bottomPadding = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom * 2 + getDimenInt(R.dimen.margin_xx_large)
    }
    fabClear.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//    viewModel.loadHistory()

    setFragmentResultListener(RESULT_ARRIVED) { _, _ ->
      if (activity?.isFinishing != true) {
        ArrivedDialog.newInstance().show(parentFragmentManager, ArrivedDialog::class.java.simpleName)
      }
    }
  }

  override fun FragmentHistoryBinding.initViews() {
    rvHistory.layoutManager = GridLayoutManager(context, if (isTablet()) 2 else 1, GridLayoutManager.VERTICAL, false)
    fabClear.setOnClickListener {
      createDialog(
        type = HistoryDialogType.CLEAR,
        yesAction = { viewModel.clearHistory() },
      )
    }
  }

  override fun HistoryViewModel.initObservers() {
//    collectWithLifecycle(itemRemoved) {
//      binding.rvHistory.adapter?.takeIf { it is HistoryAdapter }?.let { adapter ->
//        val size = (adapter as HistoryAdapter).removeItem(it)
//        if (size == 0) viewModel.setNoHistory()
//      }
//    }
//    collectWithLifecycle(historyCleared) {
//      binding.rvHistory.adapter?.takeIf { it is HistoryAdapter }?.let { adapter ->
//        (adapter as HistoryAdapter).clear()
//        viewModel.setNoHistory()
//      }
//    }

//    collectWithLifecycle(noHistory) {
//      binding.tvNoHistory.visibility = if (it) View.VISIBLE else View.GONE
//      binding.fabClear.visibility = if (!it) View.VISIBLE else View.GONE
//    }
//    collectWithLifecycle(allHistory.combineTransform(locale) { history, locale -> emit(history to locale) }) { historyAndLocale ->
//      val historyItems = historyAndLocale.first
//      val locale = historyAndLocale.second

//      binding.rvHistory.adapter = HistoryAdapter(
//        currentLocale = locale,
//        onItemClickAction = ::onHistoryItemClick,
//        onMenuClickAction = ::onHistoryMenuClick
//      )
//        .apply {
//          setHasStableIds(false)
//          submitList(historyItems)
//        }
//        .also { adapter ->
//          val ctx = context ?: return@also
//          val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(ctx) { position ->
//            val history = historyItems[position]
//            createDialog(
//              type = HistoryDialogType.REMOVE,
//              yesAction = { viewModel.removeItem(history) },
//              noAction = { adapter.notifyItemChanged(position) },
//            )
//          })
//          itemTouchHelper.attachToRecyclerView(binding.rvHistory)
//        }
//    }
//    collectWithLifecycle(loading) { binding.progressLoading.visibility = if (it) View.VISIBLE else View.GONE }
  }

  private fun onHistoryMenuClick(history: History) {
    createDialog(
      type = HistoryDialogType.REMOVE,
      yesAction = { viewModel.removeItem(history) },
    )
  }

  private fun onHistoryItemClick(history: History) {
    createDialog(
      type = HistoryDialogType.RESTORE,
      yesAction = {
//        router.navigateTo(
//          searchScreen(
//            bundleOf(
//              ARG_FROM_ID to history.fromStop.id,
//              ARG_TO_ID to history.toStop.id,
//              ARG_ADD_TO_HISTORY to false
//            )
//          )
//        )
      },
    )
  }

  private fun createDialog(
    type: HistoryDialogType,
//    history: History? = null,
    yesAction: () -> Unit,
    noAction: () -> Unit = {}
  ) {
    DialogHistory.newInstance(
      bundleOf(
        ARG_HISTORY_DESCRIPTION to when (type) {
          HistoryDialogType.CLEAR -> R.string.message_history_dialog_clear
          HistoryDialogType.REMOVE -> R.string.message_history_dialog_delete
          HistoryDialogType.RESTORE -> R.string.message_history_dialog_restore
          HistoryDialogType.UNDEFINED -> return
        },
        ARG_HISTORY_DIALOG_TYPE to type.ordinal
      )
    ).apply {
      onYesClick = { yesAction() }
      onNoClick = {
        noAction()
        dismiss()
      }
    }.let { dialog ->
      if (activity?.isFinishing != true) {
        dialog.show(parentFragmentManager, DialogHistory::class.java.simpleName)
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = HistoryFragment()
  }
}
