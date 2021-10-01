package robert.findtransport.presentation.feedback

import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.databinding.FragmentFeedbackBinding
import robert.findtransport.utils.extensions.*
import robert.findtransport.utils.viewbinding.viewBinding

class FeedbackFragment : BaseFragment<FeedbackViewModel, FragmentFeedbackBinding>() {
  override val binding: FragmentFeedbackBinding by viewBinding(FragmentFeedbackBinding::inflate)
  override val viewModel: FeedbackViewModel by viewModel()

  override fun FragmentFeedbackBinding.initInsets() {
    appBar.onWindowInsets { v, windowInsets ->
      v.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
    }
    fabSend.onWindowInsets { v, windowInsets ->
      v.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom + getDimenInt(R.dimen.fab_margin)
    }
  }

  override fun AppCompatActivity.initActionBar() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    setHasOptionsMenu(true)
  }

  override fun FragmentFeedbackBinding.initViews() {
    tvFeedbackLabel.setFeedbackMessage()
    inputEmail.doAfterTextChanged { viewModel.onEmailInput(it) }
    inputSubject.doAfterTextChanged { viewModel.onSubjectInput(it) }
    inputMessage.doAfterTextChanged { viewModel.onMessageInput(it) }
    fabSend.setOnClickListener {
      viewModel.sendFeedback()
    }
  }

  override fun FeedbackViewModel.initObservers() {
    observe(feedbackSent) {
      showToast(getString(R.string.feedback_sent))
      onBackPressed()
    }
    observe(showHideLoading) { binding.flLoading.visibility = if (it) View.VISIBLE else View.GONE }
    observe(errorEmail) { binding.ilEmail.setCustomError(it) }
    observe(errorSubject) { binding.ilSubject.setCustomError(it) }
    observe(errorMessage) { binding.ilMessage.setCustomError(it) }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_settings, menu.apply { clear() })
  }

  companion object {
    fun newInstance() = FeedbackFragment()
  }
}
