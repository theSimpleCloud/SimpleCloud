package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.base.manager.setup.provider.ServiceJavaCommandAnswerProvider
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class ServerGroupSetupWithJava : ServerGroupSetup() {

    @SetupQuestion(12, "manager.setup.service-versions.question.java", ServiceJavaCommandAnswerProvider::class)
    fun useJavaCommand(javaCommandType: String): Boolean {
        this.javaCommand = javaCommandType
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.java.success")
        return true
    }
}