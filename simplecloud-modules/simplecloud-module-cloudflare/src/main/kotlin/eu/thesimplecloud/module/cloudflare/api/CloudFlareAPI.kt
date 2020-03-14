package eu.thesimplecloud.module.cloudflare.api

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.cloudflare.config.Config
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:48
 */
class CloudFlareAPI(val config: Config) {

    val CLOUD_FLARE_API_URL = "https://api.cloudflare.com/client/v4/"

    val aRecords = HashMap<String, String>()
    val services = HashMap<UUID, String>()

    fun createForAlreadyStartedServices() {
        CloudAPI.instance.getCloudServiceManager().getAllCloudServices().filter { it.isProxy() }.forEach {
            createForService(it)
        }
    }

    fun createForService(service: ICloudService) {
        val dnsID = createSRVRecord(service)

    }

    fun createSRVRecord(service: ICloudService): String {
        if (!aRecords.containsKey(service.getGroupName())) {
            //val recordID = createRecord(service, getDefaultARecord(service))
        }

        return TODO()
    }

    private fun createRecord(service: ICloudService, jsonData: JsonData): String? {
        try {
            val httpURLConnection = URL(CLOUD_FLARE_API_URL + "zones/"
                    + config.zoneID + "/dns_records").openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.doOutput = true
            httpURLConnection.useCaches = false
            httpURLConnection.setRequestProperty("X-Auth-Email", config.email)
            httpURLConnection.setRequestProperty("X-Auth-Key", config.apiToken)
            httpURLConnection.setRequestProperty("Accept", "application/json")
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            DataOutputStream(httpURLConnection.outputStream).use { dataOutputStream ->
                dataOutputStream.writeBytes(jsonData.getAsJsonString())
                dataOutputStream.flush()
            }
            if (httpURLConnection.responseCode < 400) httpURLConnection.inputStream else httpURLConnection.errorStream.use { stream ->
                val result = JsonData.fromInputStream(stream)
                httpURLConnection.disconnect()
                val resultPath = result.getPath("result")
                if (result.getBoolean("success")!! && resultPath != null) {
                    return resultPath.getString("id")
                } else {
                    try {
                        val array = result.getAsJsonArray("errors")
                        if (array.size() == 0) {
                            return null
                        }
                        if (array[0].asJsonObject["code"].asLong == 81057L) {
                            return null
                        }
                    } catch (ignored: Throwable) {
                    }
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

}