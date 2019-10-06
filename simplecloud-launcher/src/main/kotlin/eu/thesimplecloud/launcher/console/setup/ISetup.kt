package eu.thesimplecloud.launcher.console.setup

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.09.2019
 * Time: 14:21
 */
interface ISetup {

    /**
     * Returns a list of all SetupQuestions.
     * The questions will be asked in the lists order
     */
    fun questions(): List<ISetupQuestion>

    /**
     * Called when the setup is completed.
     */
    fun onFinish()

}