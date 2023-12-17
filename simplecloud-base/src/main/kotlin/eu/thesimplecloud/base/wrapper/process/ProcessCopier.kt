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

package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.FileFinder
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIODeleteFile
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOUnzipZipFile
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.clientserverapi.lib.util.ZipUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * Date: 09.06.2020
 * Time: 14:32
 * @author Frederick Baier
 */
class ProcessCopier(val cloudService: ICloudService) {

    private val blockedCopyFileNames = arrayListOf(
        "SIMPLE-CLOUD.json",
        "SimpleCloud-Plugin.jar",
        "SimpleCloud-Extension.jar"
    )

    fun copy(path: String): ICommunicationPromise<Unit> {
        val serviceProcess = Wrapper.instance.cloudServiceProcessManager
            .getCloudServiceProcessByServiceName(cloudService.getName())
        serviceProcess ?: return CommunicationPromise.failed(IllegalStateException("Cannot copy inactive service"))

        val tempDirectory = serviceProcess.getTempDirectory()
        val dirToCopy = if (path == ".") tempDirectory else File(tempDirectory, path)
        if (!dirToCopy.exists())
            return CommunicationPromise.failed(IllegalArgumentException("Directory does not exist"))
        if (!dirToCopy.isDirectory)
            return CommunicationPromise.failed(IllegalArgumentException("The specified file must be a directory"))
        return if (Wrapper.instance.isStartedInManagerDirectory()) {
            copyUsingFiles(dirToCopy, tempDirectory)
        } else {
            copyUsingNetty(dirToCopy, tempDirectory)
        }
    }

    private fun copyUsingNetty(dirToCopy: File, tempDirectory: File): ICommunicationPromise<Unit> {
        val zippedTemplatesDir = File(DirectoryPaths.paths.zippedTemplatesPath)
        val zipFile = File(zippedTemplatesDir, cloudService.getName() + ".zip")
        zipFile.parentFile.mkdirs()
        ZipUtils.zipFiles(zipFile, FileFinder.getAllFiles(dirToCopy)
            .filter { !isBlockedCopyFile(it.name) }, tempDirectory.path + "/")

        //send file
        val savePath = DirectoryPaths.paths.zippedTemplatesPath + "T-" + cloudService.getName() + ".zip"
        val dirToUnzip = cloudService.getTemplate().getDirectory().path
        return Wrapper.instance.connectionToManager.sendFile(zipFile, savePath, 20 * 1000)
            .thenDelayed(1200, TimeUnit.MILLISECONDS) {
                val relativePathInTemplate = dirToCopy.path.replace(tempDirectory.path, "")
                val dirToReplace = dirToUnzip + if (relativePathInTemplate == "/.") "" else relativePathInTemplate
                //delete folder to replace
                Wrapper.instance.connectionToManager.sendUnitQuery(PacketIODeleteFile(dirToReplace))
            }.flatten(10 * 1000).thenDelayed(1000, TimeUnit.MILLISECONDS) {
                //unzip file
                Wrapper.instance.connectionToManager.sendUnitQuery(
                    PacketIOUnzipZipFile(savePath, dirToUnzip),
                    10 * 1000
                )
            }.flatten().throwFailure()

    }

    private fun copyUsingFiles(dirToCopy: File, tempDirectory: File): ICommunicationPromise<Unit> {
        val templateDir = cloudService.getTemplate().getDirectory()
        val relativePathInTemplate = dirToCopy.path.replace(tempDirectory.path, "")
        val dirToCopyTo = File(templateDir, relativePathInTemplate)
        FileUtils.copyDirectory(dirToCopy, dirToCopyTo) { !isBlockedCopyFile(it.name) }
        return CommunicationPromise.of(Unit)
    }

    private fun isBlockedCopyFile(fileName: String): Boolean {
        return this.blockedCopyFileNames.contains(fileName)
                || Wrapper.instance.existingModules.map { it.file.name }.contains(fileName)
    }

}