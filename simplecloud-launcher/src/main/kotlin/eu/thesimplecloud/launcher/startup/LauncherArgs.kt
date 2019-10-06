package eu.thesimplecloud.launcher.startup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class LauncherArgs() : CliktCommand() {

    val checkForUpdates: Boolean by option(help = "Checks for updates.").flag()
    val startApplication: String? by option(help = "Starts an application directly.")

    override fun run() {
        println(checkForUpdates)
        println(startApplication)
    }
}