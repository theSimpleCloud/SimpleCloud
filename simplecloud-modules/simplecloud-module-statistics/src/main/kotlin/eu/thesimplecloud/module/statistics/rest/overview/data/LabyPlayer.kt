package eu.thesimplecloud.module.statistics.rest.overview.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

data class LabyPlayer(
    var image: String? = null,
    var name: String? = null,
    var joins: Int = 0,
    var uuid: UUID? = null,
) : LabyResource<UUID>, Comparable<LabyPlayer> {

    @Transient private var apiUrl = "https://laby.net/api/v3/user/%s/profile"

    override fun createRequestURL(args: UUID): String {
        apiUrl = apiUrl.format(args)
        this.uuid = args
        return apiUrl
    }

    override fun retrieve(client: OkHttpClient): Boolean {
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.body == null || response.code != 200) return false
        val body = response.body!!.string()
        val bodyJson = JsonParser.parseString(body).asJsonObject
        response.close()
        if(!bodyJson.has("username")) return false
        this.name = bodyJson.get("username").asString
        if (!bodyJson.has("textures")) return false
        val textures = bodyJson.getAsJsonObject("textures")
        if (!textures.has("SKIN")) return false
        val skins = textures.getAsJsonArray("SKIN")
        if (!skins.isJsonArray) return false
        var activeSkin = JsonObject()
        skins.forEach { skin ->
            val skinObj = skin.asJsonObject
            if (skinObj.has("active") && skinObj.get("active").asBoolean)
            {
                activeSkin = skinObj
            }
        }
        if(!activeSkin.has("image_hash")) return false
        this.image = activeSkin.get("image_hash").asString



        return true
    }

    override fun compareTo(other: LabyPlayer): Int {
        return if (other.joins > joins) 1 else if (other.joins < joins) -1 else 0
    }

}
