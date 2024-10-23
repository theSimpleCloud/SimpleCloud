package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.base.manager.dump.DumpFileCreator
import eu.thesimplecloud.base.manager.dump.DumpUploader
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath

/**
 * Created by MrManHD
 * Class create at 14.11.23 16:24
 */

@Command("dump", CommandType.CONSOLE_AND_INGAME, "cloud.command.dump")
class DumpCommand : ICommandHandler {

    @CommandSubPath(description = "Creates and uploads a new dump")
    fun execute(sender: ICommandSender) {
        sender.sendMessage("A new dump is being created... This can take a few seconds!")
        DumpUploader.uploadFile(DumpFileCreator().create())
            .thenAccept { sender.sendMessage("A new dump has been created under $it") }
            .exceptionally {
                sender.sendMessage("The dump could not be uploaded! Is the upload server online?")
                null
            }
    }

}