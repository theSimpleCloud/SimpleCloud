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