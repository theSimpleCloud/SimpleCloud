package eu.thesimplecloud.module.cloudflare.api

import com.google.gson.JsonArray
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.cloudflare.config.CloudFlareData
import eu.thesimplecloud.module.cloudflare.config.Config
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:48
 */
class CloudFlareAPI(val config: Config) {

    private val CLOUD_FLARE_API_URL = "https://api.cloudflare.com/client/v4/"

    private val aRecords = ArrayList<CloudFlareRecord>()
    private val dnsCache = ArrayList<CloudFlareDNSCache>()

    fun createForAlreadyStartedServices() {
        CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().filter { it.isProxy() }.forEach {
            createForService(it)
        }
    }

    fun createForService(service: ICloudService) {
        config.cloudFlareDatas.filter { it.groupName.equals(service.getGroupName(), true) }.forEach {
            val dns = createSRVRecord(service, it)?: return
            dnsCache.add(CloudFlareDNSCache(dns, service.getUniqueId()))
        }
    }

    private fun createSRVRecord(service: ICloudService, cloudFlareData: CloudFlareData): String? {
        if (getRecordsByServiceName(service.getGroupName()).isEmpty()) {
            val recordID = createRecord(getDefaultARecord(service, cloudFlareData), cloudFlareData)

            if (recordID != null) {
                aRecords.add(CloudFlareRecord(service.getName(), recordID))
            }
        }

        return createRecord(getDefaultConfig(service, cloudFlareData), cloudFlareData)
    }

    fun deleteRecord(service: ICloudService) {
        config.cloudFlareDatas.filter { it.groupName.equals(service.getGroupName(), true) }.forEach {data ->
            dnsCache.filter { it.uuid == service.getUniqueId() }.forEach {
                deleteRecord(it.dns, data)
            }
            dnsCache.removeAll {it.uuid == service.getUniqueId()}
        }
    }

    fun deleteRecord(dnsID: String, cloudFlareData: CloudFlareData) {
        try {
            val httpURLConnection = URL(CLOUD_FLARE_API_URL + "zones/"
                    + cloudFlareData.zoneID + "/dns_records/" + dnsID).openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "DELETE"
            httpURLConnection.useCaches = false
            httpURLConnection.setRequestProperty("X-Auth-Email", cloudFlareData.email)
            httpURLConnection.setRequestProperty("X-Auth-Key", cloudFlareData.apiToken)
            httpURLConnection.setRequestProperty("Accept", "application/json")
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            if (httpURLConnection.responseCode < 400) httpURLConnection.inputStream
            httpURLConnection.disconnect()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun createRecord(jsonData: JsonData, cloudFlareData: CloudFlareData): String? {
        try {
            val connection = URL(CLOUD_FLARE_API_URL + "zones/"
                    + cloudFlareData.zoneID + "/dns_records").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.useCaches = false
            connection.setRequestProperty("X-Auth-Email", cloudFlareData.email)
            connection.setRequestProperty("X-Auth-Key", cloudFlareData.apiToken)
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Content-Type", "application/json")
            DataOutputStream(connection.outputStream).use { dataOutputStream ->
                dataOutputStream.writeBytes(jsonData.getAsJsonString())
                dataOutputStream.flush()
            }
            if (connection.responseCode < 400) connection.inputStream else connection.errorStream.use { stream ->
                val result = JsonData.fromInputStream(stream)
                connection.disconnect()
                val resultPath = result.getPath("result")
                if (result.getBoolean("success")!! && resultPath != null) {
                    return resultPath.getString("id")
                } else {
                    try {
                        val array: JsonArray = result.getAsJsonArray("errors")!!
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

    private fun getDefaultConfig(service: ICloudService, cloudFlareData: CloudFlareData): JsonData {
        val name = if (cloudFlareData.subDomain.equals("@")) cloudFlareData.domainName else cloudFlareData.subDomain

        return JsonData().append("type", "SRV").
        append("name", "_minecraft._tcp.${cloudFlareData.domainName}").
        append("content", "SRV 1 1 " + service.getPort().toString() + " " + service.getName() + "." + cloudFlareData.domainName).
        append("ttl", 1).
        append("proxied", false).
        append("data", JsonData()
                .append("service", "_minecraft")
                .append("proto", "_tcp")
                .append("name", name)
                .append("priority", 1)
                .append("weight", 1)
                .append("port", service.getPort().toString())
                .append("target", "${service.getName()}.${cloudFlareData.domainName}")
                .getAsJsonString())
    }

    private fun getDefaultARecord(service: ICloudService, cloudFlareData: CloudFlareData): JsonData {
        return JsonData().append("type", "A").
                append("name", "${service.getName()}.${cloudFlareData.domainName}").
                append("content", service.getHost()).
                append("ttl", 1).
                append("proxied", false).
                append("data", JsonData().getAsJsonString())
    }

    private fun getRecordsByServiceName(serviceName: String): List<CloudFlareRecord> {
        return aRecords.filter { it.serviceName == serviceName }
    }

}