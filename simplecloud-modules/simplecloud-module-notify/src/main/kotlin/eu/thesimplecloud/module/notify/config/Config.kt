package eu.thesimplecloud.module.notify.config

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 13.04.2020
 * Time: 16:27
 */
data class Config(
        val serviceStartingMessage: String,
        val serviceStartedMessage: String,
        val serviceStoppedMessage: String,
        val hoverMessage: String
)