package eu.thesimplecloud.launcher.screens

import eu.thesimplecloud.api.screen.ICommandExecutable
import java.util.concurrent.CopyOnWriteArrayList

class ScreenImpl(private val commandExecutable: ICommandExecutable) : IScreen {

    private val messages = CopyOnWriteArrayList<String>()

    override fun getCommandExecutable(): ICommandExecutable = this.commandExecutable

    fun addMessage(message: String) {
        this.messages.add(message)
        if (this.messages.size > 100) {
            this.messages.removeAt(0)
        }
    }

    override fun getAllSavedMessages(): List<String> = this.messages

}