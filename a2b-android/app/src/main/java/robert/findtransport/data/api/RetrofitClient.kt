package robert.findtransport.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import robert.findtransport.BuildConfig
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.extensions.md5
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {
  companion object {
    private var retrofit: Retrofit? = null

    @OptIn(ExperimentalSerializationApi::class)
    fun getClient(): Retrofit = retrofit ?: Retrofit.Builder().run {
      baseUrl(BASE_URL)
      addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
      client(getOkHttpClient())
      build().also { retrofit = it }
    }

    private fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder().run {
      readTimeout(100, TimeUnit.SECONDS)
      connectTimeout(100, TimeUnit.SECONDS)
      callTimeout(100, TimeUnit.SECONDS)
      writeTimeout(100, TimeUnit.SECONDS)
      connectionSpecs(
        listOf(
          ConnectionSpec.MODERN_TLS,
          ConnectionSpec.COMPATIBLE_TLS,
          ConnectionSpec.RESTRICTED_TLS,
          ConnectionSpec.CLEARTEXT
        )
      )
      addInterceptor(Interceptor { chain ->
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val header = md5(BuildConfig.KEY_PREFIX, date)

        val newRequest: Request = chain.request().newBuilder()
          .addHeader("a2bkey", "Bearer $header")
          .build()

        chain.proceed(newRequest)
      })
      build()
    }
  }
}
