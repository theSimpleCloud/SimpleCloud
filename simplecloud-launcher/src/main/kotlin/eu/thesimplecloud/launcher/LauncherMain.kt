package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.dependency.LauncherDependencyLoader
import eu.thesimplecloud.launcher.startup.LauncherStartArguments

fun main(args: Array<String>) {
    LauncherDependencyLoader().loadLauncherDependencies()
    LauncherStartArguments().main(args)
}