package robert.findtransport.presentation.compose.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.robertlevonyan.compose.buttontogglegroup.ColumnToggleButtonGroup
import kotlinx.coroutines.flow.collectLatest
import robert.findtransport.R
import robert.findtransport.data.service.LocaleService
import robert.findtransport.presentation.compose.navigation.NavigationScreens
import robert.findtransport.presentation.compose.reusables.*

@Composable
fun IntroScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  introViewModel: IntroViewModel = hiltViewModel(),
) {

  val currentLanguageIndex by introViewModel.currentLanguageIndex.collectAsState()
  val context = LocalContext.current
  var welcomeMessage by rememberSaveable { mutableStateOf(R.string.message_slider_welcome) }

  LaunchedEffect(key1 = null) {
    introViewModel.locale.collectLatest { language ->
      LocaleService(context).changeLocale(language)
      welcomeMessage = 0
      welcomeMessage = R.string.message_slider_welcome
    }
  }

  Scaffold(modifier = modifier.fillMaxSize()) { contentPadding ->
    ConstraintLayout(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
    ) {
      val (bg, title, hello, buttons, next) = createRefs()

      Image(
        modifier = Modifier.constrainAs(bg) {
          width = Dimension.matchParent
          height = Dimension.matchParent
          start.linkTo(parent.start)
          top.linkTo(parent.top)
          end.linkTo(parent.end)
          bottom.linkTo(parent.bottom)
        },
        painter = painterResource(id = R.drawable.ob1),
        contentDescription = null,
      )

      TitleAndLogo(
        modifier = Modifier
          .wrapContentSize()
          .constrainAs(title) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
          }
          .padding(DoublePadding)
      )

      Text(
        modifier = Modifier
          .wrapContentSize()
          .constrainAs(hello) {
            start.linkTo(parent.start)
            top.linkTo(title.bottom)
            end.linkTo(parent.end)
            bottom.linkTo(buttons.top)
          },
        text = stringResource(id = welcomeMessage),
        fontWeight = FontWeight.SemiBold,
        fontSize = Text24,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
      )

      ColumnToggleButtonGroup(
        modifier = Modifier
          .fillMaxWidth(fraction = 0.8f)
          .wrapContentHeight()
          .constrainAs(buttons) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
          },
        buttonCount = 3,
        primarySelection = currentLanguageIndex,
        selectedColor = Accent.copy(alpha = 0.2f),
        selectedContentColor = MaterialTheme.colors.onSurface,
        unselectedContentColor = MaterialTheme.colors.onSurface,
        borderColor = Accent.copy(alpha = 0.5f),
        buttonTexts = arrayOf(
          stringResource(id = R.string.settings_language_am),
          stringResource(id = R.string.settings_language_en),
          stringResource(id = R.string.settings_language_ru),
        ),
        buttonIcons = arrayOf(
          painterResource(id = R.drawable.ic_lng_arm),
          painterResource(id = R.drawable.ic_lng_eng),
          painterResource(id = R.drawable.ic_lng_rus),
        ),
        buttonIconTint = Color.Transparent,
        unselectedButtonIconTint = Color.Transparent,
      ) { index ->
        introViewModel.setLanguage(index)
      }

      FloatingActionButton(
        modifier = Modifier.constrainAs(next) {
          width = Dimension.wrapContent
          height = Dimension.wrapContent
          start.linkTo(parent.start)
          top.linkTo(buttons.bottom)
          end.linkTo(parent.end)
          bottom.linkTo(parent.bottom)
        },
        onClick = {
          introViewModel.setIntroPassed()
          navController.navigate(NavigationScreens.HomeScreen.name) {
            popUpTo(navController.graph.id)
          }
        }) {
        Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
      }
    }
  }
}

@Composable
private fun TitleAndLogo(
  modifier: Modifier,
) {
  Row(modifier = modifier.wrapContentSize()) {
    Image(
      modifier = Modifier
        .scale(0.75f)
        .align(CenterVertically),
      painter = painterResource(id = R.drawable.ic_logo),
      contentDescription = null,
    )
    Text(
      modifier = modifier
        .padding(HalfPadding)
        .align(CenterVertically),
      text = stringResource(id = R.string.app_name),
      color = MaterialTheme.colors.onSurface,
      fontSize = TextIntroLabel,
      fontWeight = FontWeight.Bold,
    )
  }
}


