package robert.findtransport.data.service

import android.content.Context
import android.content.res.Resources
import java.util.Locale


class LocaleService(private val context: Context) {

    @Suppress("DEPRECATION")
    fun changeLocale(lang: String) {
        val appLocale = Locale(lang)
        val res: Resources = context.resources
        val conf = res.configuration
        conf.locale = appLocale
        Locale.setDefault(appLocale)
        val dm = res.displayMetrics
        res.updateConfiguration(conf, dm)
    }
}
