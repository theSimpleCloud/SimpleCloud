package eu.thesimplecloud.api.service.version.v3

import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.api.service.version.type.ServiceVersionType

/**
 * @author Niklas Nieberler
 */

data class ServiceVersionManifest(
    val name: String,
    val type: ServiceVersionType,
    val isPaperclip: Boolean?,
    val latestVersion: String,
    val latestSnapshot: String?,
    val downloadLinks: List<DownloadLink>
) {

    data class DownloadLink(
        val version: String,
        val link: String
    )

    fun toServiceVersions(): List<ServiceVersion> {
        return this.downloadLinks.map {
            ServiceVersion(
                getServiceName(it),
                getServiceAPIType(),
                it.link,
                this.isPaperclip ?: false
            )
        }
    }

    private fun getServiceName(downloadLink: DownloadLink): String {
        return "${name}_${downloadLink.version}"
            .replace("_latest", "")
            .replace(" ", "_")
            .replace(".", "_")
            .uppercase()
    }

    private fun getServiceAPIType(): ServiceAPIType {
        if (this.name == "Velocity")
            return ServiceAPIType.VELOCITY
        return when (this.type) {
            ServiceVersionType.PROXY -> ServiceAPIType.BUNGEECORD
            ServiceVersionType.SERVER -> ServiceAPIType.SPIGOT
        }
    }

}