package eu.thesimplecloud.launcher.console.setup

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.setu.ISetup

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.09.2019
 * Time: 14:20
 */
class SetupManager(val launcher: Launcher) {

    val logger = launcher.logger
    var currentSetup: ISetup? = null
    var currentQuestion: ISetupQuestion? = null
    var currentQuestionIndex: Int = 0

    fun startSetup(setup: ISetup) {
        currentSetup = setup;
        currentQuestion = setup.questions().get(0)
        logger.info("Setup started." + if (launcher.consoleManager.application is Launcher) "" else " You can quit the setup by writing \"exit\"!" )
    }

    fun cancelSetup() {
        currentSetup = null;
        logger.warning("Setup canceled")
    }

    fun finishSetup() {
        logger.success("Setup completed")
        currentSetup?.onFinish()
        currentSetup = null;
    }

    fun onResponse(message: String) {
        val response = currentQuestion?.onResponseReceived(message)
        if (response != null) {
            if (response) {
                nextQuestion()
            } else if (!response) {
                Launcher.instance.logger.warning("Invalid answer!")
            }
        }
    }

    fun nextQuestion() {
        val activeSetup = currentSetup
        if (activeSetup == null)
            return
        if (!hasNextQuestion()) {
            finishSetup()
            return
        }
        currentQuestionIndex++
        currentQuestion = activeSetup.questions().get(currentQuestionIndex)
        launcher.logger.updatePromt(false)
    }

    fun hasNextQuestion(): Boolean {
        val activeSetup = currentSetup
        if (activeSetup == null)
            return false
        return activeSetup.questions().size > currentQuestionIndex + 1
    }

}