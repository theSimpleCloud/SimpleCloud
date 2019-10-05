package eu.thesimplecloud.lib.service.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import java.lang.UnsupportedOperationException
import java.util.*

class DefaultCloudService(
        private val groupName: String,
        private val serviceNumber: Int,
        private val uniqueId: UUID,
        private val templateName: String,
        private val wrapperName: String,
        private val port: Int,
        private var motd: String
) : ICloudService {

    private var serviceState = ServiceState.PREPARED
    private var onlinePlayers = 0
    private var authenticated = false

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

    override fun start() = CloudLib.instance.getCloudServiceManger().startServices(this)

    override fun shutdown() = CloudLib.instance.getCloudServiceManger().stopServices(this)


}