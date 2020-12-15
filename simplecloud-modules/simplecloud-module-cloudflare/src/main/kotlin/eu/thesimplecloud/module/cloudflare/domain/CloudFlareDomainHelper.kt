/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.cloudflare.domain

import eu.roboflax.cloudflare.CloudflareAccess
import eu.roboflax.cloudflare.CloudflareRequest
import eu.roboflax.cloudflare.constants.Category
import eu.roboflax.cloudflare.objects.dns.DNSRecord
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.cloudflare.config.domain.DomainConfig
import eu.thesimplecloud.module.cloudflare.config.proxy.ProxyConfig
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by IntelliJ IDEA.
 * Date: 13.12.2020
 * Time: 22:42
 * @author Frederick Baier
 */

class CloudFlareDomainHelper(
    val config: DomainConfig
) {

    private val cfAccess = CloudflareAccess(config.apiToken, config.email)

    private val serviceToDNSId = ConcurrentHashMap<ICloudService, String>()

    fun isCloudFlareConfiguredCorrectly(): CompletableFuture<Boolean> {
        val request = CloudflareRequest(Category.SSL_VERIFICATION, cfAccess)
            .identifiers(config.zoneId)
        val future = request.sendAsync()
        return future.thenApply { it.errors.isEmpty() }
    }

    fun createARecordsForWrappersIfNotExist(wrappers: List<IWrapperInfo>) {
        val request = CloudflareRequest(Category.LIST_DNS_RECORDS, cfAccess)
            .identifiers(config.zoneId)

        val records: List<DNSRecord> = request.asObjectList(DNSRecord::class.java).`object`
        wrappers.forEach { wrapper ->
            checkARecord(wrapper, records)
        }
    }

    private fun checkARecord(wrapper: IWrapperInfo, records: List<DNSRecord>) {
        val wrapperRecord =
            records.firstOrNull { it.name.equals(getFullDomainByWrapper(wrapper), true) && it.type == "A" }
        if (wrapperRecord == null) {
            createARecord(wrapper)
            return
        }
        if (wrapperRecord.content != wrapper.getHost()) {
            deleteRecord(wrapperRecord.id)
            createARecord(wrapper)
        }
    }

    fun deleteAllSRVRecordsAndWait() {
        val clonedKeys = HashSet(this.serviceToDNSId.keys)
        clonedKeys.map { deleteSRVRecord(it) }.combineAllPromises().syncUninterruptibly()
    }

    fun deleteSRVRecord(service: ICloudService): ICommunicationPromise<Unit> {
        val id = this.serviceToDNSId[service] ?: return CommunicationPromise.UNIT_PROMISE
        this.serviceToDNSId.remove(service)
        return deleteRecord(id)
    }

    fun deleteRecord(id: String): ICommunicationPromise<Unit> {
        return CommunicationPromise.runAsync {
            val request = CloudflareRequest(Category.DELETE_DNS_RECORD, cfAccess)
                .identifiers(config.zoneId, id)
            request.send()
            return@runAsync
        }
    }

    fun createSRVRecord(service: ICloudService, proxyConfig: ProxyConfig): ICommunicationPromise<Unit> {
        return CommunicationPromise.runAsync {
            val request = CloudflareRequest(Category.CREATE_DNS_RECORD, cfAccess)
                .identifiers(config.zoneId)
                .body(createSRVRecordBody(service, proxyConfig).jsonElement)
            val response = request.asObject(DNSRecord::class.java)
            if (response.errors.isNotEmpty()) {
                Launcher.instance.logger.warning("Error creating SRV record:")
                Launcher.instance.logger.warning(response.errors.toString())
            } else {
                val id = response.`object`.id
                serviceToDNSId[service] = id
            }
        }
    }

    private fun createARecord(wrapper: IWrapperInfo) {
        val request = CloudflareRequest(Category.CREATE_DNS_RECORD, cfAccess)
            .identifiers(config.zoneId)
            .body(createARecordBody(wrapper).jsonElement)
        val response = request.send()
        if (response.errors.isNotEmpty()) {
            Launcher.instance.logger.warning("Error creating A record:")
            Launcher.instance.logger.warning(response.errors.toString())
        }
    }

    private fun createARecordBody(wrapper: IWrapperInfo): JsonLib {
        return JsonLib.empty()
            .append("type", "A")
            .append("ttl", 1)
            .append("proxied", false)
            .append("name", getFullDomainByWrapper(wrapper))
            .append("content", Launcher.instance.launcherConfig.host)

    }


    private fun createSRVRecordBody(service: ICloudService, proxyConfig: ProxyConfig): JsonLib {
        return JsonLib.empty()
            .append("type", "SRV")
            .append("ttl", 1)
            .append("proxied", false)
            .append(
                "data", JsonLib.empty()
                    .append("service", "_minecraft")
                    .append("proto", "_tcp")
                    .append("name", proxyConfig.subDomain)
                    .append("priority", 0)
                    .append("weight", 0)
                    .append("port", service.getPort())
                    .append("target", getFullDomainByWrapper(service.getWrapper()))
            )
    }

    private fun getFullDomainByWrapper(wrapper: IWrapperInfo): String {
        return "${wrapper.getName()}.simplecloud.${config.domain}"
    }
}




