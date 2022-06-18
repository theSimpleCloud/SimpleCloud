package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.config.java.JavaVersion
import eu.thesimplecloud.launcher.config.java.JavaVersionConfigLoader
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class JavaSetup : ISetup {

    lateinit var name: String
    lateinit var path: String

    @SetupQuestion(0, "manager.setup.service-versions.question.javaName")
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
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.java.success")
        val javaVersion = JavaVersion.paths
        javaVersion.versions[name] = path
        JavaVersionConfigLoader().saveConfig(javaVersion)
    }

}