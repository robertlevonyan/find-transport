package robert.findtransport.presentation.screens.home.components

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.google.android.play.core.review.ReviewManagerFactory
import robert.findtransport.R
import robert.findtransport.presentation.reusables.activity.LocalActivity
import robert.findtransport.presentation.reusables.composables.BlankButton
import robert.findtransport.presentation.reusables.composables.RegularButton
import robert.findtransport.presentation.reusables.composables.TextSecondary
import robert.findtransport.presentation.reusables.theme.HalfPadding

@Composable
fun RateDialog(
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
) {
    fun rate(activity: Activity) {
        val reviewManager = ReviewManagerFactory.create(activity)
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("Rate: ", request.result.toString())
                    } else {
                        Log.e("Error: ", it.exception.toString())
                    }
                }
            } else {
                Log.e("Error: ", request.exception.toString())
            }
        }
    }

    Card {
        Column(
            modifier = Modifier
                .padding(HalfPadding)
                .fillMaxWidth()
        ) {
            TextSecondary(
                text = stringResource(id = R.string.message_rate),
                textAlign = TextAlign.Start,
            )

            Row(modifier = Modifier.align(Alignment.End)) {
                val activity = LocalActivity.current
                RegularButton(text = stringResource(id = R.string.label_yes)) {
                    onPositiveClick()
                    rate(activity)
                }
                BlankButton(text = stringResource(id = R.string.label_no)) { onNegativeClick() }
            }
        }
    }
}