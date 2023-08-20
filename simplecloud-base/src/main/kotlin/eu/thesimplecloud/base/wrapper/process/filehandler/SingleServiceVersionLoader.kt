/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.runner.utils.Downloader
import org.apache.commons.io.FileUtils
import java.io.File

class SingleServiceVersionLoader(
    private val serviceVersion: ServiceVersion
) {

    fun load(): LoadedServiceVersion {
        if (this.serviceVersion.isPaperClip) {
            return loadPaperClipVersion()
        }
        return loadNormalVersion()
    }

    private fun loadNormalVersion(): LoadedServiceVersion {
        val expectedFile = File(DirectoryPaths.paths.minecraftJarsPath + serviceVersion.name + ".jar")
        if (!expectedFile.exists()) {
            downloadNormalVersion(expectedFile)
        }
        return LoadedServiceVersion(expectedFile, expectedFile.name)
    }

    private fun downloadNormalVersion(expectedFile: File) {
        Downloader().userAgentDownload(this.serviceVersion.downloadURL, expectedFile)
        //delete json to prevent bugs in spigot version 1.8
        //ZipUtils().deletePath(expectedFile, "com/google/gson/")
        //Thread.sleep(200)
    }

    private fun loadPaperClipVersion(): LoadedServiceVersion {
        val expectedDir = File(DirectoryPaths.paths.minecraftJarsPath + serviceVersion.name + "/")
        if (!expectedDir.exists()) {
            downloadAndExecutePaperclip(expectedDir)
        }
        return LoadedServiceVersion(expectedDir, "paperclip.jar")
    }

    private fun downloadAndExecutePaperclip(expectedDir: File) {
        val paperClipFile = File(expectedDir, "paperclip.jar")
        downloadPaperclip(paperClipFile)
        executePaperClip(paperClipFile, expectedDir)
        deleteUnnecessaryFiles(expectedDir)
    }

    private fun deleteUnnecessaryFiles(expectedDir: File) {
        val unnecessaryFileNames = listOf("eula.txt", "server.properties", "logs")
        for (fileName in unnecessaryFileNames) {
            val file = File(expectedDir, fileName)
            if (file.isDirectory) {
                FileUtils.deleteDirectory(file)
            } else {
                file.delete()
            }
        }
    }

    private fun downloadPaperclip(paperClipFile: File) {
        Downloader().userAgentDownload(this.serviceVersion.downloadURL, paperClipFile)
    }

    private fun executePaperClip(file: File, workingDir: File) {
        Launcher.instance.logger.info("Executing paperclip....")
        val processBuilder = ProcessBuilder("java", "-jar", file.absolutePath)
        processBuilder.directory(workingDir)
        processBuilder.start().waitFor()
        Launcher.instance.logger.info("Executed paperclip")
    }

}