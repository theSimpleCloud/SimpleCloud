package eu.thesimplecloud.module.statistics.rest.overview.data

import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

data class LabyServer(
    var niceName: String? = null,
    var serverIp: String? = null,
) : LabyResource<UUID> {

    @Transient private var apiUrl = "https://laby.net/api/v3/user/%s/game-stats"
    override fun createRequestURL(args: UUID): String {
        apiUrl = apiUrl.format(args)
        return apiUrl
    }

    override fun retrieve(client: OkHttpClient) : Boolean {
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()
        val response = client.newCall(request).execute()
        if(response.body == null || response.code != 200) return false
        val body = response.body!!.string()
        val bodyJson = JsonParser.parseString(body).asJsonObject
        response.close()
        if(!bodyJson.has("most_played_server")) return false
        val serverObject = bodyJson.getAsJsonObject("most_played_server")
        if(!serverObject.has("nice_name") || !serverObject.has("direct_ip")) return false
        val serverIp = serverObject.get("direct_ip").asString
        val niceName = serverObject.get("nice_name").asString
        if(serverIp.equals("unknown") || niceName.equals("unknown")) return false
        this.niceName = niceName
        this.serverIp = serverIp
        return true
    }

}
