package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.api.depedency.Dependency
import eu.thesimplecloud.api.utils.ManifestLoader
import eu.thesimplecloud.launcher.LauncherMain
import eu.thesimplecloud.launcher.invoker.MethodInvokeHelper
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.net.URLClassLoader
import kotlin.concurrent.thread

class LauncherUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        File("launcher-update.jar")
) {

    override fun getCurrentVersion(): String {
        return getCurrentLauncherVersion()
    }

    override fun executeJar() {
        val file = File("launcher-update.jar")
        val runningJar = File(Launcher::class.java.protectionDomain.codeSource.location.toURI())
        val mainClass = ManifestLoader.getMainClass(file.absolutePath)
        val newClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        val mainMethod = newClassLoader.loadClass(mainClass).getMethod("main", Array<String>::class.java)
        Thread.currentThread().contextClassLoader = newClassLoader
        Runtime.getRuntime().addShutdownHook(Thread {
            val updaterFile = File("storage/updater.jar")
            val dependency = Dependency("eu.thesimplecloud.simplecloud", "simplecloud-updater", getLatestVersion()!!)
            dependency.download(getRepositoryURL(), updaterFile)
            val processBuilder = ProcessBuilder("java", "-jar", "storage/updater.jar", "1500", runningJar.absolutePath, file.absolutePath)
            processBuilder.directory(File("."))
            processBuilder.start()
        })
        mainMethod.invoke(null, LauncherMain.specifiedArguments)
    }
}