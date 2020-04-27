package eu.thesimplecloud.module.cloudflare.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.02.2020
 * Time: 17:37
 */
data class Config(
        val cloudFlareDatas: List<CloudFlareData>
) {

    fun getCloudFlareDataByDomainName(domainName: String): CloudFlareData? {
        return cloudFlareDatas.firstOrNull { it.domainName == domainName }
    }
}