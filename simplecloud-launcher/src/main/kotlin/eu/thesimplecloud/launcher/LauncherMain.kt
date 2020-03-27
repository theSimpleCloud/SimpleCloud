package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.dependency.LauncherDependencyLoader
import eu.thesimplecloud.launcher.startup.LauncherStartArguments

class LauncherMain {
    companion object {
        var specifiedArguments: Array<String>? = null
    }
}

fun main(args: Array<String>) {
    LauncherMain.specifiedArguments = args
    println("Starting launcher...")
    LauncherDependencyLoader().loadLauncherDependencies()
    LauncherStartArguments().main(args)
}