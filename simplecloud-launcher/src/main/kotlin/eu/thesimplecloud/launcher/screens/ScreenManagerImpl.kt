package eu.thesimplecloud.launcher.screens

import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.CopyOnWriteArraySet

class ScreenManagerImpl : IScreenManager {

    private val screens = CopyOnWriteArraySet<ScreenImpl>()

    private var activeScreen: IScreen? = null

    override fun registerScreen(commandExecutable: ICommandExecutable): IScreen {
        val screen = ScreenImpl(commandExecutable)
        this.screens.add(screen)
        return screen
    }

    override fun unregisterScreen(name: String) {
        this.screens.removeIf { it.getName().equals(name, true) }
    }

    override fun addScreenMessage(commandExecutable: ICommandExecutable, message: String) {
        val screen = getScreen(commandExecutable.getName()) ?: registerScreen(commandExecutable)
        screen as ScreenImpl
        screen.addMessage(message)
        if (activeScreen == screen) {
            Launcher.instance.logger.empty(message)
        }
    }

    override fun getAllScreens(): Set<IScreen> = this.screens

    override fun getActiveScreen(): IScreen? = this.activeScreen

    override fun setActiveScreen(screen: IScreen?) {
        this.activeScreen = screen
    }

    override fun leaveActiveScreen() {
        Launcher.instance.screenManager.setActiveScreen(null)
        Launcher.instance.clearConsole()
    }

}