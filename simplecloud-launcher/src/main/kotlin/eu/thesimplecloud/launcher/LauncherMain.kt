package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.dependency.LauncherDependencyLoader
import eu.thesimplecloud.launcher.startup.StartArguments

fun main(args: Array<String>) {
    LauncherDependencyLoader().loadLauncherDependencies()
    StartArguments().main(args)
}