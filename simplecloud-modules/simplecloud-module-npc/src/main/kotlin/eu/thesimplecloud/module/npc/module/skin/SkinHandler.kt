package eu.thesimplecloud.module.npc.module.skin

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SkinHandler {

    private val requestExecutor: Executor = Executors.newSingleThreadExecutor()

    private val endpointGetSkinByID = "https://api.mineskin.org/get/uuid/%s"
    private val endpointGetUUIDByName = "https://api.mineskin.org/validate/name/%s"
    private val endpointGetSkinByUUID = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"

    fun getSkinConfigByID(id: String): CompletableFuture<SkinConfig?> {
        return processRequest(id, endpointGetSkinByID, this::parseSkinConfigByID)
    }

    fun getSkinConfigByName(name: String): CompletableFuture<SkinConfig?> {
        val uuid = getUUIDFromName(name)?.toString() ?: return CompletableFuture.completedFuture(null)
        return processRequest(uuid, endpointGetSkinByUUID, this::parseSkinConfigByName)
    }

    private fun processRequest(
        param: String,
        endpointTemplate: String,
        parser: (JsonObject) -> SkinConfig?,
    ): CompletableFuture<SkinConfig?> {
        val endpoint = String.format(endpointTemplate, param)
        return CompletableFuture.supplyAsync({
            try {
                val jsonObject = getJsonObjectFromEndpoint(endpoint)
                parser(jsonObject)
            } catch (exception: Exception) {
                exception.printStackTrace()
                null
            }
        }, requestExecutor)
    }

    private fun getJsonObjectFromEndpoint(endpoint: String): JsonObject {
        val result = getRequestResponse(endpoint)
        val jsonElement = JsonParser.parseString(result)
        return jsonElement.asJsonObject
    }

    private fun getRequestResponse(endpoint: String): String {
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw RuntimeException("Failed : HTTP error code : $responseCode")
        }

        val input = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        return input
    }

    private fun parseSkinConfigByID(jsonObject: JsonObject): SkinConfig? {
        val texture = jsonObject.getAsJsonObject("data")?.getAsJsonObject("texture")
        return texture?.let { SkinConfig(it["value"].asString, it["signature"].asString) }
    }

    private fun parseSkinConfigByName(jsonObject: JsonObject): SkinConfig? {
        val properties = jsonObject.getAsJsonArray("properties")?.get(0)?.asJsonObject
        return properties?.let { SkinConfig(it["value"].asString, it["signature"].asString) }
    }

    private fun getUUIDFromName(name: String): UUID? {
        val endpoint = String.format(endpointGetUUIDByName, name)
        val jsonObject = getJsonObjectFromEndpoint(endpoint)
        if (!jsonObject["valid"].asBoolean) return null
        val uuidString = jsonObject["uuid"].asString
        return formatUuid(uuidString)
    }

    private fun formatUuid(uuidString: String): UUID {
        val formattedUuid = uuidString.replace(
            "(.{8})(.{4})(.{4})(.{4})(.{12})".toRegex(), "$1-$2-$3-$4-$5"
        )
        return UUID.fromString(formattedUuid)
    }

    data class SkinConfig(val value: String, val signature: String)
}
