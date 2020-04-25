package eu.thesimplecloud.module.notify

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceJoinableEvent
import eu.thesimplecloud.api.event.service.CloudServiceRegisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 13.04.2020
 * Time: 17:03
 */
class CloudListener(val module: NotifyModule) : IListener {

    private val permission = "simplecloud.module.notify.messages"

    @CloudEventHandler
    fun on(event: CloudServiceRegisteredEvent) {
        sendMessage(event.cloudService, module.config.serviceStartingMessage, false)
    }

    @CloudEventHandler
    fun on(event: CloudServiceJoinableEvent) {
        sendMessage(event.cloudService, module.config.serviceStartedMessage, true)
    }

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        sendMessage(event.cloudService, module.config.serviceStoppedMessage, false)
    }

    private fun sendMessage(service: ICloudService, message: String, addClick: Boolean) {
        val serviceName = service.getName()
        val replacedMessage = message.replace("%SERVICE%", serviceName)

        val cloudText = CloudText(replacedMessage)
        if (addClick) {
            cloudText.addHover(module.config.hoverMessage)
            cloudText.addClickEvent(CloudText.ClickEventType.RUN_COMMAND, "/server " + service.getName())
        }

        CloudAPI.instance.getCloudPlayerManager().getAllCachedCloudPlayers().forEach { cloudPlayer ->
            cloudPlayer.hasPermission(permission).then {
                if (it) {
                    cloudPlayer.sendMessage(cloudText)
                }
            }
        }
    }

}