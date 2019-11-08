package eu.thesimplecloud.base.manager.screens

import eu.thesimplecloud.lib.screen.ICommandExecutable

class ScreenImpl(private val commandExecutable: ICommandExecutable) : IScreen {

    override fun getCommandExecutable(): ICommandExecutable = this.commandExecutable
}