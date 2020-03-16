package eu.thesimplecloud.base.manager.commands.ingame

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("hub", CommandType.INGAME)
class HubCommand : ICommandHandler {

    @CommandSubPath
    fun handleCommand(sender: ICommandSender) {
        val player = sender as ICloudPlayer
        if (player.getConnectedServer()?.isLobby() == true) {
            player.sendMessage("ingame.command.hub.already-lobby", "&cYou are already on a lobby server.")
            return
        }
        player.sendToLobby()
    }

}