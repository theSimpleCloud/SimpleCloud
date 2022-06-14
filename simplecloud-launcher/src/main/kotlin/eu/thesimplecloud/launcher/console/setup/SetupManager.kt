/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.console.setup

import eu.thesimplecloud.api.parser.string.StringParser
import eu.thesimplecloud.api.utils.containsIgnoreCase
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.setup.annotations.SetupCancelled
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.replace
import eu.thesimplecloud.launcher.startup.Launcher
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class SetupManager(val launcher: Launcher) {

    val logger = launcher.logger
    val setupQueue = ArrayList<SetupData>()
    var currentSetup: SetupData? = null
    var currentQuestion: SetupQuestionData? = null
        private set
    private var currentQuestionIndex = 0

    private var setupsCompletedPromise = CommunicationPromise<Unit>(enableTimeout = false)

    fun queueSetup(setup: ISetup, first: Boolean = false) {
        val questions = ArrayList<SetupQuestionData>()
        val methods = setup::class.java.methods
        methods.filter { it.isAnnotationPresent(SetupQuestion::class.java) }.forEach { method ->
            check(method.parameters.size == 1) { "Function marked with SetupQuestion must have one parameter." }
            questions.add(
                SetupQuestionData(
                    method.getAnnotation(SetupQuestion::class.java),
                    method,
                    method.parameters[0]
                )
            )
        }

        val setupFinishedMethods = methods.filter { it.isAnnotationPresent(SetupFinished::class.java) }
        val setupCancelledMethods = methods.filter { it.isAnnotationPresent(SetupCancelled::class.java) }
        check(setupFinishedMethods.size <= 1) { "Only one function in a setup can be marked with SetupFinished." }
        check(setupCancelledMethods.size <= 1) { "Only one function in a setup can be marked with SetupCancelled." }
        val finishedMethod = setupFinishedMethods.firstOrNull()
        val cancelledMethod = setupCancelledMethods.firstOrNull()
        finishedMethod?.let { check(it.parameters.isEmpty()) { "The function marked with SetupFinished must have 0 parameters." } }
        cancelledMethod?.let { check(it.parameters.isEmpty()) { "The function marked with SetupFinished must have 0 parameters." } }

        val setupData =
            SetupData(setup, cancelledMethod, finishedMethod, questions.sortedBy { it.setupQuestion.number })
        if (this.currentSetup == null) {
            startSetup(setupData)
            return
        }
        if (first)
            this.setupQueue.add(0, setupData)
        else
            this.setupQueue.add(setupData)
    }

    fun hasActiveSetup(): Boolean {
        return this.currentSetup != null
    }

    private fun startSetup(setupData: SetupData) {
        this.currentSetup = setupData
        this.currentQuestion = setupData.questions[currentQuestionIndex]

        Launcher.instance.clearConsole()
        Launcher.instance.logger.setup("Setup started. Type `exit` to leave the setup.")
        printCurrentQuestion()
    }

    private fun printCurrentQuestion() {
        val currentQuestion = this.currentQuestion
            ?: throw IllegalStateException("There is no setup at the moment")
        val suggestionProvider = currentQuestion.setupQuestion.answerProvider.java.newInstance()
        //search suggestions for an empty input
        val suggestions = suggestionProvider.getSuggestions(launcher.consoleSender)
            .replace("", "<empty>")
        val suffix = if (suggestions.isNotEmpty()) "§ePossible answers: " + suggestions.joinToString() else ""
        if (suffix.isEmpty()) {
            launcher.consoleSender.sendPropertyInSetup(currentQuestion.setupQuestion.property)
        } else {
            launcher.consoleSender.sendPropertyInSetup(currentQuestion.setupQuestion.property)
            Launcher.instance.logger.setup(suffix)
        }
    }

    fun onResponse(response: String) {
        val currentQuestion = this.currentQuestion ?: return
        val parsedValue = StringParser().parseToObject(response, currentQuestion.parameter.type)
        val possibleAnswers = currentQuestion.setupQuestion.answerProvider.java.newInstance()
            .getSuggestions(launcher.consoleSender)
        if (parsedValue is String) {
            if (possibleAnswers.isNotEmpty() && !possibleAnswers.containsIgnoreCase(parsedValue)) {
                Launcher.instance.logger.setup("Invalid response")
                return
            }
        }
        val invokeResponse = try {
            currentQuestion.method.invoke(this.currentSetup!!.source, parsedValue)
        } catch (e: Exception) {
            Launcher.instance.logger.setup("Invalid response")
            return
        }
        if (invokeResponse is Boolean && invokeResponse == false) {
            return
        }
        nextQuestion()
    }


    private fun nextQuestion() {
        val activeSetup = this.currentSetup ?: return
        if (!hasNextQuestion(activeSetup)) {
            finishCurrentSetup()
            return
        }
        this.currentQuestionIndex++
        this.currentQuestion = activeSetup.questions[currentQuestionIndex]
        //this.logger.updatePrompt(false)
        printCurrentQuestion()
    }

    private fun finishCurrentSetup() {
        Launcher.instance.clearConsole()

        val currentSetupReference = this.currentSetup
        resetSetup()

        if (this.setupQueue.isEmpty()) {
            Launcher.instance.logger.printCachedMessages()
        }

        currentSetupReference?.callFinishedMethod()
        this.logger.success("Setup completed.")
        checkForNextSetup()
    }

    fun cancelCurrentSetup() {
        Launcher.instance.clearConsole()

        val currentSetupReference = this.currentSetup
        resetSetup()

        if (this.setupQueue.isEmpty()) {
            Launcher.instance.logger.printCachedMessages()
        }

        currentSetupReference?.callCancelledMethod()
        this.logger.warning("Setup cancelled.")
        checkForNextSetup()
    }

    private fun checkForNextSetup() {
        if (this.setupQueue.isEmpty()) {
            this.setupsCompletedPromise.trySuccess(Unit)
            this.setupsCompletedPromise = CommunicationPromise(enableTimeout = false)
        }
        if (this.setupQueue.isNotEmpty()) {
            startSetup(this.setupQueue.removeAt(0))
        }
    }

    private fun resetSetup() {
        this.currentSetup = null
        this.currentQuestion = null
        this.currentQuestionIndex = 0
    }

    private fun hasNextQuestion(setupData: SetupData) = this.currentQuestionIndex + 1 in setupData.questions.indices

    fun waitFroAllSetups() {
        if (this.currentSetup != null)
            this.setupsCompletedPromise.awaitUninterruptibly()
    }

    fun getSetupSuggestions(userInput: String, consoleSender: ConsoleSender): List<String> {
        if (!hasActiveSetup()) {
            throw IllegalStateException("There is no active setup")
        }

        val suggestions = currentQuestion!!.setupQuestion.answerProvider.java.newInstance()
            .getSuggestions(consoleSender)

        return suggestions.filter { it.toLowerCase().startsWith(userInput.toLowerCase()) }
    }


    class SetupData(
        val source: ISetup,
        val cancelledMethod: Method?,
        val finishedMethod: Method?,
        val questions: List<SetupQuestionData>
    ) {

        fun callFinishedMethod() {
            finishedMethod?.invoke(source)
        }

        fun callCancelledMethod() {
            cancelledMethod?.invoke(source)
        }

    }

    class SetupQuestionData(val setupQuestion: SetupQuestion, val method: Method, val parameter: Parameter)

}