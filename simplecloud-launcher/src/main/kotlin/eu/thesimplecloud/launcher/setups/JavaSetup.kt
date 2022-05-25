package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.api.javaVersions.JavaVersion
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class JavaSetup : ISetup {

    private val javaVersion = JavaVersion.paths

    @SetupQuestion(0, "Please enter Java 8 startup command (java = default)")
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
            Launcher.instance.consoleSender.sendPropertyInSetup("Path does not exist.")
            false
        }
    }

    @SetupQuestion(1, "Please enter Java 11 startup command (java = default)")
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
            Launcher.instance.consoleSender.sendPropertyInSetup("Path does not exist.")
            false
        }
    }

    @SetupQuestion(2, "Please enter Java 16 startup command (java = default)")
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
            Launcher.instance.consoleSender.sendPropertyInSetup("Path does not exist.")
            false
        }
    }

    @SetupQuestion(3, "Please enter Java 17 startup command (java = default)")
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
            Launcher.instance.consoleSender.sendPropertyInSetup("Path does not exist.")
            false
        }
    }


    @SetupQuestion(4, "Please enter Java 18 startup command (java = default)")
    fun java18(path: String): Boolean {
        javaVersion.java18 = path
        if (path.uppercase() == "JAVA") {
            setup()
            return true
        }
        return if (File(path).exists()) {
            setup()
            true
        } else {
            Launcher.instance.consoleSender.sendPropertyInSetup("Path does not exist.")
            false
        }
    }

    fun setup() {
        Launcher.instance.consoleSender.sendPropertyInSetup("Java startup command set")
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