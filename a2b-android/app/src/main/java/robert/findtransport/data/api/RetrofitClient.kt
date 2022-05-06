package robert.findtransport.data.api

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import robert.findtransport.BuildConfig
import robert.findtransport.data.entity.TransportStopJoin
import robert.findtransport.utils.BASE_URL
import robert.findtransport.utils.extensions.md5
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitClient private constructor() {
  companion object {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit = retrofit ?: Retrofit.Builder().run {
      baseUrl(BASE_URL)
      addConverterFactory(GsonConverterFactory.create(getGson()))
      client(getOkHttpClient())
      build().also { retrofit = it }
    }

    private fun getGson(): Gson {
      val type = object : TypeToken<List<TransportStopJoin>>() {}.type
      return GsonBuilder().registerTypeAdapter(
        type,
        object : JsonDeserializer<List<TransportStopJoin>> {
          override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<TransportStopJoin> {
            val list = mutableListOf<TransportStopJoin>()
            json?.run {
              val array = asJsonArray

              array.map { it.asJsonObject }.forEach { jsonObject ->
                list.add(
                  TransportStopJoin().apply {
                    id = jsonObject.get("id").asInt
                    transportId = jsonObject.get("transport_id").asInt
                    stopId = jsonObject.get("stop_id").asInt
                    reverse = jsonObject.get("reverse").asInt
                    order = jsonObject.get("position").asInt
                  }
                )
              }
            }
            return list
          }
        }).create()
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
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
          timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
        val header = md5(BuildConfig.KEY_PREFIX, date)

        val newRequest: Request = chain.request().newBuilder()
          .addHeader("a2bkey", "Bearer $header")
          .build()

        chain.proceed(newRequest)
      })
      addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
        this.level = HttpLoggingInterceptor.Level.HEADERS
      })
      build()
    }
  }
}
