@file:Suppress("SpellCheckingInspection")

package robert.findtransport.data.api

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import robert.findtransport.data.entity.Stop
import robert.findtransport.data.entity.StopLocation
import robert.findtransport.data.entity.Transport
import robert.findtransport.data.entity.TransportStopJoin

interface ApiService {
  @GET("newstops/")
  suspend fun getStops(): List<Stop>

  @GET("newlocation/")
  suspend fun getStopLocations(): List<StopLocation>

  @GET("newtransport/")
  suspend fun getTransport(): List<Transport>

  @GET("newtsjoin/")
  suspend fun getJoins(): List<TransportStopJoin>

  @GET("vernew/")
  suspend fun getVersion(): String

  @FormUrlEncoded
  @POST("feedb/")
  suspend fun sendFeedback(
    @Field("mail")
    email: String,
    @Field("subject")
    subject: String,
    @Field("message")
    message: String
  ): ResponseBody
}
