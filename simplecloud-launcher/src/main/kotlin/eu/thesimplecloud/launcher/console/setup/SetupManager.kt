package eu.thesimplecloud.launcher.console.setup

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.logger.LoggerProvider
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.09.2019
 * Time: 14:20
 */
class SetupManager(val launcher: Launcher) {

    val logger = launcher.logger
    val setupQueue = LinkedBlockingQueue<ISetup>()
    var currentSetup: ISetup? = null
    var currentQuestion: ISetupQuestion? = null
    var currentQuestionIndex: Int = 0

    /**
     * Queues a setup.
     * Note: The question will be printed as prompt in [LoggerProvider]
     */
    fun queueSetup(setup: ISetup) {
        if (this.currentSetup == null) {
            startSetup(setup)
            return
        }
        this.setupQueue.add(setup)
    }

    private fun startSetup(setup: ISetup) {
        this.currentSetup = setup
        this.currentQuestion = setup.questions()[0]
        this.logger.info("Setup started. You can quit the setup by writing \"exit\"!")
    }

    fun cancelSetup() {
        currentSetup = null
        this.logger.warning("Setup canceled")
    }

    private fun finishSetup() {
        val currentSetupReference = this.currentSetup
        this.currentSetup = null
        this.currentQuestion = null
        this.currentQuestionIndex = 0
        currentSetupReference?.onFinish()
        this.logger.success("Setup completed")
        if (!setupQueue.isEmpty()){
            startSetup(this.setupQueue.poll())
        }
    }

    fun onResponse(message: String) {
        val response = this.currentQuestion?.onResponseReceived(message)
        if (response != null) {
            if (response) {
                nextQuestion()
            } else if (!response) {
                this.logger.warning("Invalid answer!")
            }
        }
    }

    private fun nextQuestion() {
        val activeSetup = currentSetup ?: return
        if (!hasNextQuestion()) {
            finishSetup()
            return
        }
        this.currentQuestionIndex++
        this.currentQuestion = activeSetup.questions()[this.currentQuestionIndex]
        this.logger.updatePrompt(false)
    }

    private fun hasNextQuestion(): Boolean {
        val activeSetup = currentSetup ?: return false
        return activeSetup.questions().size > currentQuestionIndex + 1
    }

}