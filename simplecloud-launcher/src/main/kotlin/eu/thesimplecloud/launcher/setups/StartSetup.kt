package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupCancelled
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.lib.language.LanguageProperty
import java.lang.IllegalStateException
import kotlin.concurrent.thread

class StartSetup : ISetup {

    private var cloudApplicationType: CloudApplicationType? = null


    @SetupQuestion(0, "launcher.setup.start.question", "Do you want do start the MANAGER or the WRAPPER")
    fun setup(cloudApplicationType: CloudApplicationType) {
        this.cloudApplicationType = cloudApplicationType
    }

    @SetupFinished
    fun onFinish() {
        val cloudApplicationType = this.cloudApplicationType
        checkNotNull(cloudApplicationType) { "Cloud application type was null after start setup." }
        thread {
            Launcher.instance.startApplication(cloudApplicationType)
        }
    }

    @SetupCancelled
    fun onCancel() {
        Launcher.instance.shutdown()
    }
}