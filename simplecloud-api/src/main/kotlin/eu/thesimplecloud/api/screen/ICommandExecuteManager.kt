package eu.thesimplecloud.api.screen

interface ICommandExecuteManager {

    /**
     * Executes a command on the specified [ICommandExecutable]
     */
    fun executeCommand(commandExecutable: ICommandExecutable, command: String)

}