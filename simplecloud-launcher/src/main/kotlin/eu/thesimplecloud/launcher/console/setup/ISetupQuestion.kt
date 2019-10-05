package eu.thesimplecloud.launcher.console.setup

interface ISetupQuestion {

    fun questionName(): String
    fun onResponseReceived(answer: String): Boolean




}
