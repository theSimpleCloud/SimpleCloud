package eu.thesimplecloud.launcher.event.command

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.eventapi.ICancellable
import eu.thesimplecloud.launcher.console.command.CommandData

/**
 * This event will be called when a registered cloud command is going to be executed by a player or the console.
 */
class CommandExecuteEvent(val commandSender: ICommandSender, command: CommandData) : CommandEvent(command), ICancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean = this.cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }
}