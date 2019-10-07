package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 22:10
 */
interface ICloudApplication : IBootstrap {

    /**
     * Returns the name of this application
     */
    fun getApplicationName(): String

}