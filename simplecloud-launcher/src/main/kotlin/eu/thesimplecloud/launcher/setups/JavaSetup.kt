package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.api.javaVersions.JavaVersion
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class JavaSetup : ISetup {

    lateinit var name: String
    lateinit var path: String

    @SetupQuestion(0, "java name")
    fun javaNameSetup(name: String): Boolean {
        return if (name.isNotEmpty()) {
            this.name = name
            true
        } else {
            false
        }
    }

    @SetupQuestion(1, "manager.setup.service-versions.question.java")
    fun javaVersionSetup(path: String): Boolean {
        return if (path.isNotEmpty()) {
            this.path = path
            setup()
            true
        } else {
            false
        }
    }

    fun setup() {
        Launcher.instance.consoleSender.sendPropertyInSetup("Java startup command set")
        val javaVersion = JavaVersion.paths
        javaVersion.versions[name] = path
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