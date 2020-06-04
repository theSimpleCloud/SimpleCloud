/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

    fun getName() = "$artifactId-$version"

    fun getDownloadURL(repoUrl: String): String {
        return getUrlWithoutExtension(repoUrl) + ".jar"
    }

    fun getDownloadedFile(): File {
        return File(DEPENDENCIES_DIR, "${getName()}.jar")
    }

    fun getDownloadedPomFile(): File {
        return File(POM_DIR, "$groupId-$artifactId.pom")
    }

    fun getDownloadedLastVersionFile(): File {
        return File(POM_DIR, "$groupId-$artifactId.lastVersion")
    }


    @Throws(IOException::class)
    fun download(repoUrl: String) {
        return download(repoUrl, getDownloadedFile())
    }
    @Throws(IOException::class)
    fun download(repoUrl: String, downloadFile: File) {
        Downloader().userAgentDownload(this.getDownloadURL(repoUrl), downloadFile)
    }

    fun getPomContent(repoUrl: String): String? {
        return WebContentLoader().loadContent(getUrlWithoutExtension(repoUrl) + ".pom")
    }

    fun getMetaDataContent(repoUrl: String): String? {
        return WebContentLoader().loadContent(getMainURL(repoUrl) + "maven-metadata.xml")
    }

    fun getMainURL(repoUrl: String): String {
        return repoUrl + groupId.replace("\\.".toRegex(), "/") + "/" + artifactId + "/"
    }

    fun getUrlWithoutExtension(repoUrl: String): String {
        return getMainURL(repoUrl) + version + "/" + artifactId + "-" + version
    }

}