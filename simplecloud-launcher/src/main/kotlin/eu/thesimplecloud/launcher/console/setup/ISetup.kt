package eu.thesimplecloud.launcher.console.setu

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.setup.ISetupQuestion

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.09.2019
 * Time: 14:21
 */
interface ISetup {

    fun questions(): List<ISetupQuestion>
    fun onFinish()

}