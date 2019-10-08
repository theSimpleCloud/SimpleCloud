package eu.thesimplecloud.launcher.console.setup

import eu.thesimplecloud.lib.language.LanguageProperty

interface ISetupQuestion {

    /**
     * Returns the question
     */
    fun questionProperty(): LanguageProperty

    /**
     * Called when the used entered a response for this question.
     */
    fun onResponseReceived(answer: String): Boolean




}
