package eu.thesimplecloud.module.prefix.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 13:38
 */
data class TablistInformation(
        val groupName: String = "default",
        val color: String = "GREEN",
        val prefix: String = "§7",
        val suffix: String = "",
        val priority: Int = 99
)