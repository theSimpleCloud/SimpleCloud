package eu.thesimplecloud.module.prefix.service.spigot.configuration

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class DelayConfiguration {

    var delay: Long = 0L

    private var file: File = File("plugins/simplecloud-chat-tab", "delay.yml")
    private var configuration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)

    init {
        if (!file.exists()) {

            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            file.createNewFile()
            save()

        } else {
            load()
        }
    }

    fun save() {
        configuration["delay"] = delay
        configuration.save(file)
    }

    fun load() {
        configuration.load(file)
        delay = configuration.getLong("delay")
    }

}