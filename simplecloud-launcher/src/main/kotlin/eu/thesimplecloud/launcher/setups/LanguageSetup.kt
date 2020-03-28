package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion

class LanguageSetup : ISetup {

    private val allowedLanguages = listOf("en")

    @SetupQuestion(0, "launcher.setup.language.question", "Which language do you want to use? (en)")
    fun setup(answer: String): Boolean {
        if (allowedLanguages.contains(answer)) {
            Launcher.instance.languageManager.language = "${answer.toLowerCase()}_${answer.toUpperCase()}"
            Launcher.instance.languageManager.loadFile()
        }
        return allowedLanguages.contains(answer)
    }
}