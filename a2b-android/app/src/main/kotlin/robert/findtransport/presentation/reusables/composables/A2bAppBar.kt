package robert.findtransport.presentation.reusables.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import robert.findtransport.R
import robert.findtransport.presentation.reusables.theme.MenuVerticalOffset
import robert.findtransport.utils.extensions.openPrivacyPolicy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A2bAppBar(
    title: String,
    @DrawableRes navigationIcon: Int,
    onNavigationIconClick: () -> Unit,
    onFeedbackClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null,
    additionalActions: @Composable RowScope.() -> Unit = {},
) {
    var overflowMenuState by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(painter = painterResource(id = navigationIcon), contentDescription = null)
            }
        },
        title = {
            TextPrimary(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            additionalActions.invoke(this)

            onInfoClick?.let {
                IconButton(onClick = { onInfoClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            onFeedbackClick?.let {
                IconButton(onClick = { onFeedbackClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_feedback),
                        contentDescription = stringResource(id = R.string.action_feedback),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            IconButton(onClick = { overflowMenuState = !overflowMenuState }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = stringResource(id = R.string.action_settings),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                expanded = overflowMenuState,
                offset = DpOffset(x = 0.dp, y = MenuVerticalOffset),
                onDismissRequest = { overflowMenuState = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        context.openPrivacyPolicy()
                        overflowMenuState = false
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.action_privacy),
                            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                        )
                    })
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}
