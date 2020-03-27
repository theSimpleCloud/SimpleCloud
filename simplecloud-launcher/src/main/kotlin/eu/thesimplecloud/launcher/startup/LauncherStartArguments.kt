package eu.thesimplecloud.launcher.startup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import eu.thesimplecloud.launcher.application.CloudApplicationType
import java.io.File

class LauncherStartArguments() : CliktCommand() {

    val disableAutoUpdater: Boolean by option(help = "Disables the auto updater.").flag()
    val startApplication: CloudApplicationType? by option(help = "Starts an application directly.").enum<CloudApplicationType>()


    override fun run() {
        Launcher(this).start()
    }
}