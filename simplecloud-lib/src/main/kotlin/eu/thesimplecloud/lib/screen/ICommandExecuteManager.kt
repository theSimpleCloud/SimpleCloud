package eu.thesimplecloud.lib.screen

interface ICommandExecuteManager {

    /**
     * Executes a command on the specified [ICommandExecutable]
     */
    fun executeCommand(commandExecutable: ICommandExecutable, command: String)

}