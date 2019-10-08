package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.application.CloudApplicationStarter
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.ISetupQuestion
import eu.thesimplecloud.lib.language.LanguageProperty
import java.lang.IllegalStateException

class StartSetup : ISetup {

    private var cloudApplicationType: CloudApplicationType? = null

    override fun questions(): List<ISetupQuestion> {
        val list = ArrayList<ISetupQuestion>()
        list.add(object : ISetupQuestion {
            override fun questionProperty(): LanguageProperty = LanguageProperty("launcher.setup.start.question","Do you want do start the Manager or the Wrapper")

            override fun onResponseReceived(answer: String): Boolean {
                if (answer.equals("manager", true) || answer.equals("wrapper", true)) {
                    cloudApplicationType = CloudApplicationType.valueOf(answer.toUpperCase())
                    return cloudApplicationType != null
                }
                return false
            }
        })

        return list
    }

    override fun onFinish() {
        val cloudApplicationType = this.cloudApplicationType
        checkNotNull(cloudApplicationType) { "Cloud application type was null after start setup." }
        Launcher.instance.startApplication(cloudApplicationType)
    }

    override fun onCancel() {
        Launcher.instance.shutdown()
    }
}