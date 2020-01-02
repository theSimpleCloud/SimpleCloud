package eu.thesimplecloud.api.depedency

import eu.thesimplecloud.api.utils.Downloader
import eu.thesimplecloud.api.utils.WebContentLoader
import java.io.File
import java.io.IOException

data class Dependency(val groupId: String, val artifactId: String, val version: String) {

    companion object {

        val DEPENDENCIES_DIR = File("dependencies/")

        val POM_DIR = File("dependencies/poms/")
    }


    fun getDownloadURL(repoUrl: String): String {
        return getUrlWithoutExtension(repoUrl) + ".jar"
    }

    fun getDownloadedFile(): File {
        return File(DEPENDENCIES_DIR, "$artifactId-$version.jar")
    }

    fun getDownloadedPomFile(): File {
        return File(POM_DIR, "$groupId-$artifactId.pom")
    }

    fun getDownloadedLastVersionFile(): File {
        return File(POM_DIR, "$groupId-$artifactId.lastVersion")
    }


    @Throws(IOException::class)
    fun download(repoUrl: String) {
        Downloader().userAgentDownload(this.getDownloadURL(repoUrl), this.getDownloadedFile().absolutePath)
    }

    fun getPomContent(repoUrl: String): String? {
        return WebContentLoader().loadContent(getUrlWithoutExtension(repoUrl) + ".pom")
    }

    fun getMetaDataContent(repoUrl: String): String? {
        return WebContentLoader().loadContent(getMainURL(repoUrl) + "maven-metadata.xml")
    }

    private fun getMainURL(repoUrl: String): String {
        return repoUrl + groupId.replace("\\.".toRegex(), "/") + "/" + artifactId + "/"
    }

    fun getUrlWithoutExtension(repoUrl: String): String {
        return getMainURL(repoUrl) + version + "/" + artifactId + "-" + version
    }

}