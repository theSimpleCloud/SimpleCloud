package eu.thesimplecloud.lib.service.impl

import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.*

class DefaultCloudService(
        private val groupName: String,
        private val serviceNumber: Int,
        private val uniqueId: UUID,
        private val templateName: String,
        private val wrapperName: String,
        private val port: Int,
        private val maxMemory: Int,
        private var motd: String
) : ICloudService {

    private val startingPromise = CommunicationPromise<Unit>(GlobalEventExecutor.INSTANCE)
    private val startedPromise = CommunicationPromise<Unit>(GlobalEventExecutor.INSTANCE)
    private val closedPromise = CommunicationPromise<Unit>(GlobalEventExecutor.INSTANCE)

    private var serviceState = ServiceState.PREPARED
    private var onlinePlayers = 0
    private var authenticated = false
    @GsonExclude
    private var lastUpdate = System.currentTimeMillis()

    override fun getGroupName(): String = this.groupName

    override fun getServiceNumber(): Int = this.serviceNumber

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getTemplateName(): String = this.templateName

    override fun getWrapperName(): String = this.wrapperName

    override fun getPort(): Int = this.port

    override fun getState(): ServiceState = this.serviceState

    override fun setState(serviceState: ServiceState) {
        this.serviceState = serviceState
    }

    override fun getOnlinePlayers(): Int = this.onlinePlayers

    override fun setOnlinePlayers(amount: Int) {
        this.onlinePlayers = amount
    }

    override fun getMOTD(): String = this.motd

    override fun setMOTD(motd: String) {
        this.motd = motd
    }

    override fun isAuthenticated(): Boolean = this.authenticated

    override fun setAuthenticated(authenticated: Boolean) {
        this.authenticated = authenticated
    }

    override fun shutdown() = CloudLib.instance.getCloudServiceManger().stopService(this)

    override fun getMaxMemory(): Int = this.maxMemory

    override fun getLastUpdate(): Long = this.lastUpdate

    override fun setLastUpdate(timeStamp: Long) {
        this.lastUpdate = timeStamp
    }

    override fun startingPromise(): ICommunicationPromise<Unit> = this.startingPromise

    override fun startedPromise(): ICommunicationPromise<Unit> = this.startedPromise

    override fun closedPromise(): ICommunicationPromise<Unit> = this.closedPromise

}