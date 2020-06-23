package eu.thesimplecloud.module.internalwrapper.setup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupCancelled
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 17.06.2020
 * Time: 12:06
 */
class InternalWrapperMemorySetup(private val config: LauncherConfig) : ISetup {

    private var memory: Int = 2048

    @SetupQuestion(0, "internalwrapper.setup.memory.question.name", "How much memory should the wrapper have?")
    fun memorySetup(memory: Int): Boolean {
        this.memory = memory
        Launcher.instance.consoleSender.sendMessage("internalwrapper.setup.memory.question.memory.success", "Memory set.")
        return true
    }

    @SetupFinished
    @SetupCancelled
    fun finishedOrCancelled() {
        val wrapperInfo = DefaultWrapperInfo("InternalWrapper", config.host, 2, this.memory)
        CloudAPI.instance.getWrapperManager().update(wrapperInfo)
        Launcher.instance.consoleSender.sendMessage(true, "internalwrapper.setup.memory.finished", "InternalWrapper created.")
    }

}