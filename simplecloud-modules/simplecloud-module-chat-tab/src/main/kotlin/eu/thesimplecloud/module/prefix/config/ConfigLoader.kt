package eu.thesimplecloud.module.prefix.config

import eu.thesimplecloud.api.config.AbstractJsonLibConfigLoader
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 13:36
 */
class ConfigLoader : AbstractJsonLibConfigLoader<Config>(
    Config::class.java,
    File("modules/chat+tablist/config.json"),
    {
        Config()
    },
    true
)