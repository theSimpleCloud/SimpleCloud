package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.base.manager.setup.groups.ProxyGroupSetup
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.console.setup.provider.BooleanSetupAnswerProvider
import eu.thesimplecloud.launcher.startup.Launcher

class CreateDefaultProxyGroup : ISetup {

    @SetupQuestion(0, "Do you want to create a default Proxy-Group?", BooleanSetupAnswerProvider::class)
    fun setupProxy(answer: Boolean): Boolean {
        if (!answer) {
            return true
        }
        Launcher.instance.setupManager.queueSetup(ProxyGroupSetup())
        return true
    }
}