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

package eu.thesimplecloud.runner.dependency

import eu.thesimplecloud.runner.utils.Downloader
import eu.thesimplecloud.runner.utils.WebContentLoader
import java.io.File
import java.io.IOException

/**
 * Created by IntelliJ IDEA.
 * Date: 04.09.2020
 * Time: 16:45
 * @author Frederick Baier
 */
class AdvancedCloudDependency(groupId: String, artifactId: String, version: String) :
    CloudDependency(groupId, artifactId, version) {

    private fun getDownloadURL(repoUrl: String): String {
        return getUrlWithoutExtension(repoUrl) + ".jar"
    }

    fun getDownloadedFile(): File {
        return File(DEPENDENCIES_DIR, "${getName()}.jar")
    }

    fun getDownloadedInfoFile(): File {
        return File(DEPENDENCY_INFO_DIR, "${getName()}.info")
    }

    fun existInRepo(repoUrl: String): Boolean {
        return WebContentLoader().loadContent(getDownloadURL(repoUrl)) != null
    }

    @Throws(IOException::class)
    fun download(repoUrl: String) {
        return download(repoUrl, getDownloadedFile())
    }

    @Throws(IOException::class)
    fun download(repoUrl: String, downloadFile: File) {
        Downloader().userAgentDownload(this.getDownloadURL(repoUrl), downloadFile)
    }

    private fun getMainURL(repoUrl: String): String {
        return repoUrl + groupId.replace("\\.".toRegex(), "/") + "/" + artifactId + "/"
    }

    private fun getUrlWithoutExtension(repoUrl: String): String {
        return getMainURL(repoUrl) + version + "/" + artifactId + "-" + version
    }

    fun getDependencyWithNewerVersion(other: AdvancedCloudDependency): AdvancedCloudDependency {
        val dependencyVersion = getVersionStringAsIntArray(this.version)
        val otherDependencyVersion = getVersionStringAsIntArray(other.version)
        if (dependencyVersion[0] > otherDependencyVersion[0]) return this
        if (otherDependencyVersion[0] > dependencyVersion[0]) return other

        if (dependencyVersion[1] > otherDependencyVersion[1]) return this
        if (otherDependencyVersion[1] > dependencyVersion[1]) return other

        if (dependencyVersion[2] > otherDependencyVersion[2]) return this
        if (otherDependencyVersion[2] > dependencyVersion[2]) return other

        return this
    }

    private fun getVersionStringAsIntArray(version: String): Array<Int> {
        val versionParts = version.split(".")
        val major = versionParts[0].toInt()
        val minor = parseVersionPart(versionParts.getOrNull(1))
        val patch = parseVersionPart(versionParts.getOrNull(2))
        return arrayOf(major, minor, patch)
    }

    private fun parseVersionPart(part: String?): Int {
        if (part == null) return 0
        val charArray = part.toCharArray()
        val numbers = mutableListOf<Int>()
        for (char in charArray) {
            if (char.isDigit()) {
                numbers.add(char.toInt())
            } else {
                break
            }
        }
        if (numbers.isEmpty()) return 0
        return numbers.joinToString("").toInt()
    }


    companion object {

        val DEPENDENCIES_DIR = File("dependencies/")

        val DEPENDENCY_INFO_DIR = File("dependencies/info/")


        fun fromCoords(coords: String): AdvancedCloudDependency {
            val split = coords.split(":")
            require(split.size == 3)
            return AdvancedCloudDependency(split[0], split[1], split[2])
        }
    }
}