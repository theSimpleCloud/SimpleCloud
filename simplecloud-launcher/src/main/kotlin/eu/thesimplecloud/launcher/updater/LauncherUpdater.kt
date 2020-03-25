package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class LauncherUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        File("launcher-update.jar")
) {

    override fun getCurrentVersion(): String {
        return getCurrentLauncherVersion()
    }

    override fun executeJar() {
        val processBuilder = ProcessBuilder("java", "-jar", "launcher-update.jar", "--update")
        processBuilder.directory(File("."))
        processBuilder.start()
        Launcher.instance.shutdown()
    }
}