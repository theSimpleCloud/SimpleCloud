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

package eu.thesimplecloud.base.manager.update.converter

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 18.06.2020
 * Time: 12:38
 * @author Frederick Baier
 */
class VersionConversionManager {

    private val converters = listOf<IVersionConverter>(Converter_2_0_To_2_1(), Converter_2_2_To_2_3())
    private val lastStartedVersionFile = File(DirectoryPaths.paths.storagePath + "versions/lastStartedVersion.json")

    fun convertIfNecessary() {
        getConvertersToExecute().forEach { converter ->
            executeConverter(converter)
        }
        writeLastStartedVersion()
    }

    private fun executeConverter(converter: IVersionConverter) {
        Launcher.instance.logger.info("Running converter to version 2.${converter.getTargetMinorVersion()}...")
        try {
            converter.convert()
        } catch (e: Exception) {
            throw ConversionException(converter.getTargetMinorVersion(), e)
        }
        Launcher.instance.logger.info("Converted")
    }

    private fun getConvertersToExecute(): ArrayList<IVersionConverter> {
        val lastStartedVersion = getLastStartedVersion()
        val currentVersion = Launcher.instance.getCurrentVersion()
        val lastMinorVersion = getMinorVersionFromVersionString(lastStartedVersion)
        val currentMinorVersion = getMinorVersionFromVersionString(currentVersion)
        val list = ArrayList<IVersionConverter>()
        for (i in lastMinorVersion until currentMinorVersion) {
            val version = getConverterByToVersion(i + 1)
            version?.let { list.add(version) }
        }
        return list
    }

    private fun getConverterByToVersion(version: Int): IVersionConverter? {
        return this.converters.firstOrNull { it.getTargetMinorVersion() == version }
    }

    private fun getMinorVersionFromVersionString(versionString: String): Int {
        return versionString.split(".")[1].toInt()
    }

    private fun getLastStartedVersion(): String {
        this.lastStartedVersionFile.parentFile.mkdirs()
        val config = JsonLib.fromJsonFile(this.lastStartedVersionFile) ?: return "1.2.0-BETA"
        return config.getObject(String::class.java)
    }

    private fun writeLastStartedVersion() {
        JsonLib.fromObject(Launcher.instance.getCurrentVersion()).saveAsFile(lastStartedVersionFile)
    }

    fun writeLastStartedVersionIfFileDoesNotExist() {
        if (!this.lastStartedVersionFile.exists()) {
            writeLastStartedVersion()
        }
    }

}