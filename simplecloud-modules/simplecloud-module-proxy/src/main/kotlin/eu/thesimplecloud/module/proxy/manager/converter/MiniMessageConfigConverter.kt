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

package eu.thesimplecloud.module.proxy.manager.converter

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.proxy.manager.converter.convert3to4.MessageConverter
import java.io.File

/**
 * Date: 19.06.22
 * Time: 13:05
 * @author Frederick Baier
 *
 */
class MiniMessageConfigConverter {

    private val configDir = File("modules/proxy")
    private val configFile = File(configDir, "config.json")

    fun convertIfNecessary() {
        if (!this.configFile.exists())
            return
        if (isConversionNecessary()) {
            Launcher.instance.logger.console("Converting Proxy Config...")
            createConfigBackup()
            convert()
            Launcher.instance.logger.console("Proxy config converted")
        }
    }

    private fun isConversionNecessary(): Boolean {
        val lines = this.configFile.readLines()
        return lines.any { it.contains("§") }
    }

    private fun createConfigBackup() {
        val backupFile = File(configDir, "config_backup_${System.currentTimeMillis()}.json")
        this.configFile.copyTo(backupFile)
    }

    private fun convert() {
        val lines = this.configFile.readLines()
        val newLines = lines.map { MessageConverter(it).convert() }
        this.configFile.delete()
        this.configFile.writeText(newLines.joinToString("\n"))
    }

}