package robert.findtransport.presentation.reusables.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mapbox.maps.extension.style.expressions.dsl.generated.mod
import dagger.hilt.android.AndroidEntryPoint
import robert.findtransport.R
import robert.findtransport.base.MainActivity
import robert.findtransport.base.MainViewModel
import robert.findtransport.data.service.LocaleService
import robert.findtransport.presentation.reusables.theme.A2bTheme
import robert.findtransport.presentation.reusables.theme.Accent
import robert.findtransport.presentation.reusables.theme.Black
import robert.findtransport.presentation.reusables.theme.FabPadding
import robert.findtransport.presentation.reusables.theme.IllustrationSize
import robert.findtransport.presentation.reusables.theme.Shapes

@AndroidEntryPoint
class ExceptionActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by mainViewModel.theme.collectAsState()
            val currentLanguage by mainViewModel.currentLanguage.collectAsState()
            LocaleService(this).changeLocale(currentLanguage)
            val context = LocalContext.current
            A2bTheme(theme) {
                Surface(
                    modifier = Modifier
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            modifier = Modifier.width(IllustrationSize),
                            painter = painterResource(id = R.drawable.il_exception),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                        )
                        Text(
                            modifier = Modifier.padding(vertical = FabPadding),
                            text = stringResource(id = R.string.message_exception),
                            fontWeight = MaterialTheme.typography.displayLarge.fontWeight,
                            fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        )
                        Button(
                            shape = Shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Accent,
                                contentColor = Black,
                            ),
                            onClick = {
                                (context as? ComponentActivity)?.finishAffinity()
                                context.startActivity(Intent(context, MainActivity::class.java))
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_restart),
                                contentDescription = null,
                            )
                            Text(
                                text = stringResource(id = R.string.label_restart_app)
                            )
                        }
                    }
                }
            }
        }
    }
}
