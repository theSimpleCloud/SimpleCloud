package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class LauncherUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        "https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/",
        File("launcher-update.jar")
) {
    override fun getCurrentVersion(): String {
        return Launcher::class.java.getPackage().implementationVersion
    }

    override fun executeJar() {
        val processBuilder = ProcessBuilder("java", "-jar", "launcher-update.jar", "--update")
        processBuilder.directory(File("."))
        processBuilder.start()
        Launcher.instance.shutdown()
    }
}