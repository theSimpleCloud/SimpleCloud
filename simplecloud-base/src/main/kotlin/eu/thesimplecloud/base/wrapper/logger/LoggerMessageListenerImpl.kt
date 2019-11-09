package eu.thesimplecloud.base.wrapper.logger

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.client.packets.PacketOutScreenMessage
import eu.thesimplecloud.launcher.logging.ILoggerMessageListener
import eu.thesimplecloud.launcher.logging.LogType
import eu.thesimplecloud.lib.client.CloudClientType

class LoggerMessageListenerImpl : ILoggerMessageListener {

    override fun message(msg: String, logType: LogType) {
        if (Wrapper.instance.communicationClient.isOpen() && Wrapper.instance.communicationClient.getPacketIdsSyncPromise().isSuccess)
            Wrapper.instance.communicationClient.sendQuery(PacketOutScreenMessage(CloudClientType.WRAPPER, Wrapper.instance.getThisWrapper(), msg))
    }
}