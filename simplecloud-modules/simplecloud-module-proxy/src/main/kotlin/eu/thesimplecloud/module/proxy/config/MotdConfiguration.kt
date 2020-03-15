package eu.thesimplecloud.module.proxy.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:50
 */

data class MotdConfiguration(
        val firstLines: List<String>,
        val secondLines: List<String>,
        val playerInfo: List<String>?,
        val versionName: String?
)