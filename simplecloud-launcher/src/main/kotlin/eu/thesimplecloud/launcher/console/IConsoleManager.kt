package eu.thesimplecloud.launcher.console

interface IConsoleManager {

    /**
     * Starts the thread to receive user input
     */
    fun startThread()

    /**
     * Stops the thread
     */
    fun stopThread()

}