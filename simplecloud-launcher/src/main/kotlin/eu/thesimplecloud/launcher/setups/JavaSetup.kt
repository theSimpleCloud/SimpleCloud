package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.api.javaVersions.JavaVersion
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class JavaSetup : ISetup {

    private val javaVersion = JavaVersion.paths


    @SetupQuestion(0, "java8")
    fun java8(path: String): Boolean {
        javaVersion.java8 = path

        if (path.uppercase() == "JAVA") {
            setup()
            return true
        }

        return if (File(path).exists()) {
            setup()
            true
        } else {
            Launcher.instance.consoleSender.sendMessage("Java was not found.")
            false
        }
    }

    @SetupQuestion(1, "java11")
    fun java11(path: String): Boolean {

        javaVersion.java11 = path
        if (path.uppercase() == "JAVA") {
            setup()
            return true
        }
        return if (File(path).exists()) {
            setup()
            true
        } else {
            Launcher.instance.consoleSender.sendMessage("Java was not found.")
            false
        }
    }

    @SetupQuestion(2, "java16")
    fun java16(path: String): Boolean {

        javaVersion.java16 = path


        if (path.uppercase() == "JAVA") {
            setup()
            return true
        }
        return if (File(path).exists()) {
            setup()
            true

        } else {
            Launcher.instance.consoleSender.sendMessage("Java was not found.")
            false
        }
    }

    @SetupQuestion(3, "java17")
    fun java17(path: String): Boolean {
        javaVersion.java17 = path


        if (path.uppercase() == "JAVA") {
            setup()
            return true
        }
        return if (File(path).exists()) {
            setup()
            true
        } else {
            Launcher.instance.consoleSender.sendMessage("Java was not found.")
            false
        }
    }

    @SetupQuestion(4, "java18")
    fun java18(path: String): Boolean {
        val filePath = File(path)
        return if (filePath.exists()) {
            JavaVersion.paths.java18 = path
            setup()
            return true
        } else {
            Launcher.instance.consoleSender.sendMessage("Java was not found.")
            false
        }
    }


    fun setup() {
        val launcherConfig = Launcher.instance.launcherConfig
        val config = LauncherConfig(
            launcherConfig.host,
            launcherConfig.port,
            launcherConfig.language,
            launcherConfig.directoryPaths,
            javaVersion
        )
        Launcher.instance.replaceLauncherConfig(config)
    }

}