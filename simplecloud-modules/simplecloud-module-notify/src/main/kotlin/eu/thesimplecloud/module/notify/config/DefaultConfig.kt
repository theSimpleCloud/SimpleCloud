package eu.thesimplecloud.module.notify.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 13.04.2020
 * Time: 16:25
 */
class DefaultConfig {
    companion object {
        fun get(): Config {
            return Config("§8[§e»§8] §f%SERVICE%",
                    "§8[§a»§8] §f%SERVICE%",
                    "§8[§c«§8] §f%SERVICE%",
                    "§7§oClick to connect")
        }
    }
}