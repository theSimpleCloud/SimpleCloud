package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.TimeUnit

/**
 * Date: 26.10.23
 * Time: 22:21
 * @author Frederick Baier
 *
 */
class ServiceStartWarningMessageHandler {

    private val initTime = System.currentTimeMillis()

    private val messageSentForServices = arrayListOf<String>()

    fun handleNotEnoughMemoryForService(serviceName: String) {
        if (!hasEnoughTimePastSinceStart())
            return

        if (this.messageSentForServices.contains(serviceName))
            return

        this.messageSentForServices.add(serviceName)
        Launcher.instance.logger.warning("No wrapper has enough free memory to start the service $serviceName")
        Launcher.instance.scheduler.schedule({
            this.messageSentForServices.remove(serviceName)
        }, 120, TimeUnit.SECONDS)
    }

    private fun hasEnoughTimePastSinceStart(): Boolean {
        return initTime + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis()
    }

}