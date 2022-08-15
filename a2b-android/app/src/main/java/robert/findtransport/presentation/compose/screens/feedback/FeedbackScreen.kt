package robert.findtransport.presentation.compose.screens.feedback

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import robert.findtransport.R
import robert.findtransport.data.model.enums.ExceptionType
import robert.findtransport.presentation.compose.reusables.*
import robert.findtransport.presentation.compose.reusables.composables.A2bAppBar
import robert.findtransport.presentation.compose.reusables.composables.RegularButton
import robert.findtransport.presentation.compose.reusables.composables.TextMessage
import robert.findtransport.utils.extensions.showToast

@Composable
fun FeedbackScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  feedbackViewModel: FeedbackViewModel = hiltViewModel(),
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      A2bAppBar(
        title = stringResource(id = R.string.title_feedback),
        navigationIcon = R.drawable.ic_arrow_back,
        onNavigationIconClick = { navController.popBackStack() },
      )
    }
  ) { contentPadding ->
    FeedbackContent(
      modifier = Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      feedbackViewModel = feedbackViewModel,
    )
  }
}

@Composable
private fun FeedbackContent(modifier: Modifier, feedbackViewModel: FeedbackViewModel) {
  val email by feedbackViewModel.email.collectAsState()
  val subject by feedbackViewModel.subject.collectAsState()
  val message by feedbackViewModel.message.collectAsState()
  val feedbackState by feedbackViewModel.feedbackProcess.collectAsState()
  var errorEmail by rememberSaveable { mutableStateOf(-1) }
  var errorSubject by rememberSaveable { mutableStateOf(-1) }
  var errorMessage by rememberSaveable { mutableStateOf(-1) }

  when (feedbackState) {
    FeedbackSendingStatus.Idle -> Unit
    FeedbackSendingStatus.Sending -> {
      AnimatedVisibility(visible = feedbackState == FeedbackSendingStatus.Sending) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false, onClick = {})
        ) {
          CircularProgressIndicator(
            modifier = Modifier.wrapContentSize(),
            color = MaterialTheme.colors.secondary
          )
        }
      }
    }
    FeedbackSendingStatus.Sent -> LocalContext.current.showToast(R.string.feedback_sent)
    is FeedbackSendingStatus.Failure -> when ((feedbackState as FeedbackSendingStatus.Failure).type) {
      ExceptionType.ERROR_EMAIL,
      ExceptionType.WRONG_EMAIL -> errorEmail = (feedbackState as FeedbackSendingStatus.Failure).message
      ExceptionType.ERROR_SUBJECT -> errorSubject = (feedbackState as FeedbackSendingStatus.Failure).message
      ExceptionType.ERROR_MESSAGE,
      ExceptionType.SHORT_MESSAGE -> errorMessage = (feedbackState as FeedbackSendingStatus.Failure).message
      else -> Unit
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
      border = BorderStroke(1.dp, MaterialTheme.colors.surface),
      backgroundColor = MaterialTheme.colors.primary,
    ) {
      val welcomeMessage = buildAnnotatedString {
        val welcomeMessageString = stringResource(id = R.string.message_feedback)
        withStyle(SpanStyle(fontFamily = FontFamily(Font(R.font.mdf)))) {
          append(welcomeMessageString.substring(0, 1))
        }
        append(welcomeMessageString.substring(1))
      }

      TextMessage(
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
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.email.value = it
      errorEmail = -1
    }
    AnimatedVisibility(visible = errorEmail != -1) {
      if (errorEmail != -1) {
        TextMessage(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorEmail),
          color = MaterialTheme.colors.error,
        )
      }
    }

    FeedbackInput(
      modifier = Modifier.wrapContentHeight(),
      hint = R.string.hint_subject,
      text = subject,
      error = errorSubject,
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.subject.value = it
      errorSubject = -1
    }
    AnimatedVisibility(visible = errorSubject != -1) {
      if (errorSubject != -1) {
        TextMessage(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorSubject),
          color = MaterialTheme.colors.error,
        )
      }
    }

    FeedbackInput(
      modifier = Modifier.heightIn(min = FeedbackMessageBoxSize, max = FeedbackMessageBoxSize),
      hint = R.string.hint_message,
      text = message,
      singleLine = false,
      error = errorMessage,
    ) {
      feedbackViewModel.feedbackProcess.value = FeedbackSendingStatus.Idle
      feedbackViewModel.message.value = it
      errorMessage = -1
    }
    AnimatedVisibility(visible = errorMessage != -1) {
      if (errorMessage != -1) {
        TextMessage(
          modifier = Modifier.padding(horizontal = FabPadding, vertical = SmallPadding),
          text = stringResource(id = errorMessage),
          color = MaterialTheme.colors.error,
        )
      }
    }

    RegularButton(
      modifier = Modifier
        .align(CenterHorizontally)
        .padding(FabPadding),
      text = stringResource(id = R.string.label_send_feedback),
      onClick = feedbackViewModel::sendFeedback,
    )
  }
}

@Composable
private fun FeedbackInput(
  modifier: Modifier = Modifier,
  @StringRes hint: Int,
  text: String,
  singleLine: Boolean = true,
  error: Int,
  onValueChange: (String) -> Unit,
) {
  OutlinedTextField(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = HalfPadding, horizontal = FabPadding),
    value = text,
    onValueChange = onValueChange,
    singleLine = singleLine,
    shape = Shapes.medium,
    label = { Text(text = stringResource(id = hint)) },
    colors = TextFieldDefaults.outlinedTextFieldColors(
      backgroundColor = searchInputBackgroundColor(),
      focusedBorderColor = MaterialTheme.colors.surface,
      unfocusedBorderColor = MaterialTheme.colors.surface,
      disabledBorderColor = MaterialTheme.colors.surface,
      errorBorderColor = MaterialTheme.colors.error,
      cursorColor = MaterialTheme.colors.onSurface,
      focusedLabelColor = MaterialTheme.colors.onSurface,
    ),
    textStyle = TextStyle(
      color = MaterialTheme.colors.onSurface,
      fontFamily = FontFamily(Font(R.font.google_sans_regular)),
    ),
    isError = error != -1,
  )
}
