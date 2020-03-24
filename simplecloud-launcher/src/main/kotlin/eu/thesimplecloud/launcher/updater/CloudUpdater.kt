package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class CloudUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        "http://repo.thesimplecloud.eu/artifactory/gradle-dev-local/",
        File("launcher-update.jar")
) {

    override fun executeJar() {
        val processBuilder = ProcessBuilder("java", "-jar", "launcher-update.jar", "--update")
        processBuilder.directory(File("."))
        processBuilder.start()
        Launcher.instance.shutdown()
    }
}