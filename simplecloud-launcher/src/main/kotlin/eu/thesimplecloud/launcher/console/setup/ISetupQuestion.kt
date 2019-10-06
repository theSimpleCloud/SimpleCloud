package eu.thesimplecloud.launcher.console.setup

interface ISetupQuestion {

    /**
     * Returns the question
     */
    fun questionName(): String

    /**
     * Called when the used entered a response for this question.
     */
    fun onResponseReceived(answer: String): Boolean




}
