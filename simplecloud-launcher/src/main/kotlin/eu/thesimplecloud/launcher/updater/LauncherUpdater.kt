package eu.thesimplecloud.launcher.updater


import eu.thesimplecloud.api.utils.ManifestLoader
import eu.thesimplecloud.launcher.LauncherMain
import eu.thesimplecloud.launcher.invoker.MethodInvokeHelper
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File
import java.net.URLClassLoader

class LauncherUpdater : AbstractUpdater(
        "eu.thesimplecloud.simplecloud",
        "simplecloud-launcher",
        File(Launcher::class.java.protectionDomain.codeSource.location.toURI())
) {

    override fun getCurrentVersion(): String {
        return getCurrentLauncherVersion()
    }

    override fun executeJar() {
        val file = File(Launcher::class.java.protectionDomain.codeSource.location.toURI())
        val mainClass = ManifestLoader.getMainClass(file.absolutePath)
        val newClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        val mainMethod = newClassLoader.loadClass(mainClass).getMethod("main", Array<String>::class.java)
        Thread.currentThread().contextClassLoader = newClassLoader
        MethodInvokeHelper.invoke(mainMethod, null, LauncherMain.specifiedArguments)
    }
}