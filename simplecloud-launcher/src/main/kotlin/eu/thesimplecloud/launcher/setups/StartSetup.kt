package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.setu.ISetup
import eu.thesimplecloud.launcher.console.setup.ISetupQuestion

class StartSetup : ISetup {
    var startOption: String = ""

    override fun questions(): List<ISetupQuestion> {
        val list = ArrayList<ISetupQuestion>()
        list.add(object : ISetupQuestion {
            override fun questionName(): String = "Wich language do you want to use? (en, de)"

            override fun onResponseReceived(answer: String): Boolean {
                if (answer.equals("en", true) || answer.equals("de", true)) {
                    startOption = answer
                    Launcher.instance.languageManager.language = "${answer.toLowerCase()}_${answer.toUpperCase()}"
                    Launcher.instance.languageManager.loadFile()
                    return true
                }
                return false
            }

        })
        list.add(object : ISetupQuestion {
            override fun questionName(): String = "Do you want do start the Manager or the Wrapper"

            override fun onResponseReceived(answer: String): Boolean {
                if (answer.equals("manager", true) || answer.equals("wrapper", true)) {
                    startOption = answer
                    return true
                }
                return false
            }

        })

        return list
    }

    override fun onFinish() {
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        Launcher.instance.logger.info("Starting ${startOption.toLowerCase()}...")
    }
}