package eu.thesimplecloud.module.support.lib.config

import eu.thesimplecloud.api.config.AbstractJsonLibConfigLoader
import java.io.File

/**
 * Created by MrManHD
 * Class create at 07.07.2023 19:57
 */

class UploadConfigLoader : AbstractJsonLibConfigLoader<UploadConfig>(
    UploadConfig::class.java,
    File("modules/support/settings.json"),
    { UploadConfig.Default.get() },
    true
)