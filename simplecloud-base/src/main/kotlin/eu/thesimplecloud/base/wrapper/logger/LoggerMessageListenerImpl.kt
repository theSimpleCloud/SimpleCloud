package eu.thesimplecloud.base.wrapper.logger

import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.client.packets.PacketOutScreenMessage
import eu.thesimplecloud.launcher.logging.ILoggerMessageListener
import eu.thesimplecloud.launcher.logging.LogType

class LoggerMessageListenerImpl : ILoggerMessageListener {

    override fun message(msg: String, logType: LogType) {
        if (Wrapper.instance.communicationClient.isOpen() && Wrapper.instance.isWrapperNameSet())
            Wrapper.instance.communicationClient.sendUnitQueryAsync(PacketOutScreenMessage(CloudClientType.WRAPPER, Wrapper.instance.getThisWrapper(), msg))
    }
}