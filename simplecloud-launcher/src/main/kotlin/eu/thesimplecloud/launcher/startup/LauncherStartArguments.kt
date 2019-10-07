package eu.thesimplecloud.launcher.startup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.enum
import eu.thesimplecloud.launcher.application.CloudApplicationType

class LauncherStartArguments() : CliktCommand() {

    val checkForUpdates: Boolean by option(help = "Checks for updates.").flag()
    val startApplication: CloudApplicationType? by option(help = "Starts an application directly.").enum<CloudApplicationType>()

    override fun run() {
        Launcher(this).start()
    }
}