package eu.thesimplecloud.base.manager.screens

import eu.thesimplecloud.lib.screen.ICommandExecutable
import java.util.concurrent.CopyOnWriteArraySet

class ScreenManagerImpl : IScreenManager {

    private val screens = CopyOnWriteArraySet<IScreen>()

    override fun registerScreen(name: String, commandExecutable: ICommandExecutable): IScreen {
        val screen = ScreenImpl(name, commandExecutable)
        this.screens.add(screen)
        return screen
    }

    override fun getAllScreens(): Set<IScreen> = this.screens
}