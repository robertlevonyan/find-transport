package robert.findtransport.presentation.screens.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.presentation.reusables.FabPadding
import robert.findtransport.presentation.reusables.FeedbackMessageBoxSize
import robert.findtransport.presentation.reusables.SmallPadding
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.screens.feedback.components.FeedbackInput

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FeedbackContent(
  modifier: Modifier,
  feedbackViewModel: FeedbackViewModel,
  snackbarHostState: SnackbarHostState,
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val sendingText = stringResource(id = R.string.label_feedback_sending)
  val sentText = stringResource(id = R.string.feedback_sent)

  val email by feedbackViewModel.email.collectAsState()
  val subject by feedbackViewModel.subject.collectAsState()
  val message by feedbackViewModel.message.collectAsState()
  val feedbackState by feedbackViewModel.feedbackProcess.collectAsState()
  var errorEmail by rememberSaveable { mutableStateOf(-1) }
  var errorSubject by rememberSaveable { mutableStateOf(-1) }
  var errorMessage by rememberSaveable { mutableStateOf(-1) }

  when (feedbackState) {
    FeedbackSendingStatus.Idle -> Unit
    FeedbackSendingStatus.Sending -> LaunchedEffect(key1 = null) {
      snackbarHostState.showSnackbar(
        message = sendingText,
        duration = SnackbarDuration.Indefinite,
      )
    }

    FeedbackSendingStatus.Sent -> LaunchedEffect(key1 = null) {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(
        message = sentText,
        duration = SnackbarDuration.Short,
      )
    }

    is FeedbackSendingStatus.Failure -> {
      snackbarHostState.currentSnackbarData?.dismiss()
      when ((feedbackState as FeedbackSendingStatus.Failure).type) {
        ExceptionType.ERROR_EMAIL,
        ExceptionType.WRONG_EMAIL -> errorEmail =
          (feedbackState as FeedbackSendingStatus.Failure).message

        ExceptionType.ERROR_SUBJECT -> errorSubject =
          (feedbackState as FeedbackSendingStatus.Failure).message

        ExceptionType.ERROR_MESSAGE,
        ExceptionType.SHORT_MESSAGE -> errorMessage =
          (feedbackState as FeedbackSendingStatus.Failure).message

        else -> Unit
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = FabPadding, vertical = SmallPadding),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
      val welcomeMessage = buildAnnotatedString {
        val welcomeMessageString = stringResource(id = R.string.message_feedback)
        withStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.mdf)))) {
          append(welcomeMessageString.substring(0, 1))
        }
        append(welcomeMessageString.substring(1))
      }

      TextSecondary(
        modifier = Modifier
          .fillMaxWidth()
          .padding(SmallPadding),
        text = welcomeMessage,
      )
    }

    FeedbackInput(
      modifier = Modifier.wrapContentHeight(),
      hint = R.string.hint_enter_email,
      text = email,
      error = errorEmail,
      keyboardType = KeyboardType.Email,
      imeAction = ImeAction.Next,
      requestFocus = errorEmail != -1,
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.email.value = it
      errorEmail = -1
    }
    AnimatedVisibility(visible = errorEmail != -1) {
      if (errorEmail != -1) {
        TextSecondary(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorEmail),
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Start,
        )
      }
    }

    FeedbackInput(
      modifier = Modifier.wrapContentHeight(),
      hint = R.string.hint_subject,
      text = subject,
      error = errorSubject,
      imeAction = ImeAction.Next,
      requestFocus = errorSubject != -1,
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.subject.value = it
      errorSubject = -1
    }
    AnimatedVisibility(visible = errorSubject != -1) {
      if (errorSubject != -1) {
        TextSecondary(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorSubject),
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Start,
        )
      }
    }

    FeedbackInput(
      modifier = Modifier.heightIn(min = FeedbackMessageBoxSize, max = FeedbackMessageBoxSize),
      hint = R.string.hint_message,
      text = message,
      singleLine = false,
      error = errorMessage,
      imeAction = ImeAction.Send,
      keyboardActions = KeyboardActions(onSend = {
        feedbackViewModel.sendFeedback()
        keyboardController?.hide()
      }),
      requestFocus = errorMessage != -1,
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.message.value = it
      errorMessage = -1
    }
    AnimatedVisibility(visible = errorMessage != -1) {
      if (errorMessage != -1) {
        TextSecondary(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorMessage),
          color = MaterialTheme.colorScheme.error,
          textAlign = TextAlign.Start,
        )
      }
    }

    RegularButton(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(FabPadding),
      text = stringResource(id = R.string.label_send_feedback),
      onClick = {
        feedbackViewModel.sendFeedback()
        keyboardController?.hide()
      },
    )
  }
}
