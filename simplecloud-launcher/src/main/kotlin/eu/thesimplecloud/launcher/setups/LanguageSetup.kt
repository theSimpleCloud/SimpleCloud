package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.application.CloudApplicationStarter
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.ISetupQuestion

class LanguageSetup : ISetup {

    private var cloudApplicationType: CloudApplicationType? = null

    override fun questions(): List<ISetupQuestion> {
        val supportedLanguages = listOf("en", "de")
        val list = ArrayList<ISetupQuestion>()
        list.add(object : ISetupQuestion {
            override fun questionName(): String = "Which language do you want to use? (${supportedLanguages.joinToString()})"

            override fun onResponseReceived(answer: String): Boolean {
                if (supportedLanguages.contains(answer.toLowerCase())) {
                    Launcher.instance.languageManager.language = "${answer.toLowerCase()}_${answer.toUpperCase()}"
                    Launcher.instance.languageManager.loadFile()
                    return true
                }
                return false
            }

        })

        return list
    }

    override fun onFinish() {
    }
}