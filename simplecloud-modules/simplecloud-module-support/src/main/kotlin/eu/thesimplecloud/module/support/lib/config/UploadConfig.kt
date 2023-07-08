package eu.thesimplecloud.module.support.lib.config

/**
 * Created by MrManHD
 * Class create at 07.07.2023 19:55
 */

class UploadConfig(
    val uploadUrl: String
) {

    object Default {
        fun get(): UploadConfig {
            return UploadConfig("https://haste.simplecloud.app")
        }
    }

}