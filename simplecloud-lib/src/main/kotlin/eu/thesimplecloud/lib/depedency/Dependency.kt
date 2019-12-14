package eu.thesimplecloud.lib.depedency

import eu.thesimplecloud.lib.utils.Downloader
import eu.thesimplecloud.lib.utils.WebContentLoader
import java.io.File
import java.io.IOException

data class Dependency(val groupId: String, val artifactId: String, val version: String) {

    fun getDownloadURL(repoUrl: String): String {
        return getUrlWithoutExtension(repoUrl) + ".jar"
    }

    fun getDownloadedFile(): File {
        return File("dependencies/$artifactId-$version.jar")
    }

    @Throws(IOException::class)
    fun download(repoUrl: String) {
        Downloader().userAgentDownload(this.getDownloadURL(repoUrl), this.getDownloadedFile().absolutePath)
    }

    fun getPomContent(repoUrl: String): String? {
        return WebContentLoader().loadContent(getUrlWithoutExtension(repoUrl) + ".pom")
    }

    private fun getUrlWithoutExtension(repoUrl: String): String {
        return repoUrl + groupId.replace("\\.".toRegex(), "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version
    }

}