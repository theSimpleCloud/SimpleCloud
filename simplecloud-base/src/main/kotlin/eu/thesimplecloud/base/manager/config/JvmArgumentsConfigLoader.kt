package eu.thesimplecloud.base.manager.config

import eu.thesimplecloud.api.config.AbstractJsonLibConfigLoader
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.base.core.jvm.JvmArgument
import eu.thesimplecloud.base.core.jvm.JvmArgumentsConfig
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 12.06.2020
 * Time: 18:38
 */
class JvmArgumentsConfigLoader : AbstractJsonLibConfigLoader<JvmArgumentsConfig>(
        JvmArgumentsConfig::class.java,
        File(DirectoryPaths.paths.storagePath + "jvm-arguments.json"),
        { JvmArgumentsConfig(listOf(JvmArgument(listOf("all"), listOf("-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
                "-XX:-UseAdaptiveSizePolicy")))) }
)