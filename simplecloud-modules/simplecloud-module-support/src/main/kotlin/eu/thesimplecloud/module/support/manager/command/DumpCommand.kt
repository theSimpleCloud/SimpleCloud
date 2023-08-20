package eu.thesimplecloud.module.support.manager.command

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.support.lib.DumpFileCreator
import eu.thesimplecloud.module.support.manager.handler.UploadHandler

/**
 * Created by MrManHD
 * Class create at 30.06.2023 20:43
 */

@Command("dump", CommandType.CONSOLE_AND_INGAME, "cloud.command.dump")
class DumpCommand : ICommandHandler {

    private val uploadHandler = UploadHandler()

    @CommandSubPath
    fun execute(sender: ICommandSender) {
        sender.sendMessage("A new dump is being created... This can take a few seconds!")
        this.uploadHandler.uploadFile(DumpFileCreator().create())
            .thenAccept { sender.sendMessage("A new dump has been created under $it") }
            .exceptionally {
                sender.sendMessage("The dump could not be uploaded! Is the upload server online?")
                null
            }
    }

}