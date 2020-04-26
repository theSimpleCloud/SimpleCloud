package eu.thesimplecloud.base.manager.ingamecommands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.ingamecommand.SynchronizedIngameCommandNamesContainer
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.event.command.CommandRegisteredEvent
import eu.thesimplecloud.launcher.startup.Launcher

class IngameCommandUpdater : IListener {

    private val synchronizedIngameCommandNamesContainer = SynchronizedIngameCommandNamesContainer()

    init {
        CloudAPI.instance.getSingleSynchronizedObjectManager().updateObject(this.synchronizedIngameCommandNamesContainer)
        CloudAPI.instance.getEventManager().registerListener(Manager.instance, this)
    }

    fun getRegisteredCommands(): Collection<String> = this.synchronizedIngameCommandNamesContainer.names


    @CloudEventHandler
    fun on(event: CommandRegisteredEvent) {
        synchronizedIngameCommandNamesContainer.names = Launcher.instance.commandManager.getAllIngameCommandPrefixes()
        synchronizedIngameCommandNamesContainer.update()
    }


}