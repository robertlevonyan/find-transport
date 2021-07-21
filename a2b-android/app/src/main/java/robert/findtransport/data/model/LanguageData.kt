package robert.findtransport.data.model

data class LanguageData(
    val language: String,
    val languageShort: String,
    val languageShortSetting: String,
    var current: Boolean = false
)