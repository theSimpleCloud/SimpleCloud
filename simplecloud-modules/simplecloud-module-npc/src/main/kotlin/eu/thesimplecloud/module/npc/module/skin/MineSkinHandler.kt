package eu.thesimplecloud.module.npc.module.skin

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MineSkinHandler() {

    private val requestExecutor: Executor
    private val okHttpClient: OkHttpClient

    private val endpointGetSkinByID = "https://api.mineskin.org/get/uuid/%s"
    private val endpointGetUUIDByName = "https://api.mineskin.org/validate/name/%s"
    private val endpointGetSkinByUUID = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"

    init {
        requestExecutor = Executors.newSingleThreadExecutor()
        okHttpClient = OkHttpClient()
    }

    fun getSkinConfigByID(id: String): CompletableFuture<SkinConfig> {
        return CompletableFuture.supplyAsync({
            try {
                val endpoint = String.format(endpointGetSkinByID, id)

                val request: Request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .url(endpoint)
                    .get().build()

                val response: Response = okHttpClient.newCall(request).execute()
                assert(response.body != null)

                val result: String = response.body!!.string()

                val jsonElement: JsonElement = JsonParser.parseString(result)
                val jsonObject: JsonObject = jsonElement.asJsonObject

                if (jsonObject.getAsJsonObject("data") == null)
                    return@supplyAsync null

                val texture: JsonObject = jsonObject.getAsJsonObject("data").getAsJsonObject("texture")

                return@supplyAsync SkinConfig(
                    texture.get("value").asString,
                    texture.get("signature").asString
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                return@supplyAsync null
            }
        }, requestExecutor)
    }

    fun getSkinConfigByName(name: String): CompletableFuture<SkinConfig> {
        return CompletableFuture.supplyAsync({
            try {
                val uuid = this.getUUIDFromName(name) ?: return@supplyAsync null

                val endpoint = String.format(endpointGetSkinByUUID, uuid.toString())

                val request: Request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .url(endpoint)
                    .get().build()

                val response: Response = okHttpClient.newCall(request).execute()
                assert(response.body != null)

                val result: String = response.body!!.string()

                val jsonElement: JsonElement = JsonParser.parseString(result)
                val jsonObject: JsonObject = jsonElement.asJsonObject

                if (jsonObject.getAsJsonArray("properties") == null)
                    return@supplyAsync null

                val properties = jsonObject.getAsJsonArray("properties")
                val firstProperty = properties[0].asJsonObject

                val skinValue = firstProperty["value"].asString
                val signature = firstProperty["signature"].asString

                return@supplyAsync SkinConfig(
                    skinValue,
                    signature
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                return@supplyAsync null
            }
        }, requestExecutor)
    }

    private fun getUUIDFromName(name: String): UUID? {
        val endpoint = String.format(endpointGetUUIDByName, name)

        val request: Request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url(endpoint)
            .get().build()

        val response: Response = okHttpClient.newCall(request).execute()
        assert(response.body != null)

        val result: String = response.body!!.string()

        val jsonElement: JsonElement = JsonParser.parseString(result)
        val jsonObject: JsonObject = jsonElement.asJsonObject

        if (!jsonObject.get("valid").asBoolean)
            return null

        val string = jsonObject.get("uuid").asString
        val formattedUuid = string.replace(
            "(.{8})(.{4})(.{4})(.{4})(.{12})".toRegex(), "$1-$2-$3-$4-$5"
        )

        return UUID.fromString(formattedUuid)
    }


    class SkinConfig(
        val value: String,
        val signature: String
    ) {}
}