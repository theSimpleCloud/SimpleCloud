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
    /**
     * The launcher file the update was started from (the old launcher)
     */
    val update: File? by option(help = "DON'T USE THIS. It will be used by the cloud to start the launcher in update mode.").file(exists = true)

    override fun run() {
        Launcher(this).start()
    }
}