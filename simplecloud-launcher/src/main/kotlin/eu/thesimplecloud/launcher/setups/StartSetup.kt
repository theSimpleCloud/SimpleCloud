package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.setu.ISetup
import eu.thesimplecloud.launcher.console.setup.ISetupQuestion

class StartSetup : ISetup {
    var startOption: String = ""

    override fun questions(): List<ISetupQuestion> {
        val list = ArrayList<ISetupQuestion>()
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