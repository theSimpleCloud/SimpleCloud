package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.base.manager.setup.provider.ServiceJavaCommandAnswerProvider
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class LobbyGroupSetupWithJava : LobbyGroupSetup() {

    @SetupQuestion(13, "manager.setup.service-versions.question.java", ServiceJavaCommandAnswerProvider::class)
    fun useJavaCommand(javaName: String): Boolean {
        if (javaName == "default") {
            this.javaCommand = "java"
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.java.success")
            return true
        }
        this.javaCommand = javaName
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.java.success")
        return true
    }
}