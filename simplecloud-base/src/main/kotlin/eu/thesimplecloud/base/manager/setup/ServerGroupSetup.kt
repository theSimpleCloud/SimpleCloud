package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class ServerGroupSetup : ISetup {

    private lateinit var name: String

    @SetupQuestion("manager.setup.server-group.question.name", "Which name should the group have")
    fun nameQuestion(name: String): Boolean {
        this.name = name
        if (name.length > 16){
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-group.question.name.too-long", "The specified name is too long.")
            return false
        }
        return true
    }

    //TODO require template object here.
    @SetupQuestion("manager.setup.server-group.question.template", "Which template should the group have")
    fun templateQuestion(name: String): Boolean {
        this.name = name
        if (name.length > 16){
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-group.question.name.too-long", "The specified name is too long.")
            return false
        }
        return true
    }





}