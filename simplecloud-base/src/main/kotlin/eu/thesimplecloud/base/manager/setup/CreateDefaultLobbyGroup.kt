package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.base.manager.setup.groups.LobbyGroupSetup
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.console.setup.provider.BooleanSetupAnswerProvider
import eu.thesimplecloud.launcher.startup.Launcher

class CreateDefaultLobbyGroup : ISetup {

    @SetupQuestion(0, "Do you want to create a default Lobby-Group?", BooleanSetupAnswerProvider::class)
    fun setupLobby(answer: Boolean): Boolean {
        if (!answer) {
            return true
        }
        Launcher.instance.setupManager.queueSetup(LobbyGroupSetup())
        return true
    }
}