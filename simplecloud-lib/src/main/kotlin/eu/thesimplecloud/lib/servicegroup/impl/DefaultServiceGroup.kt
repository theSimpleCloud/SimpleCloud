package eu.thesimplecloud.lib.servicegroup.impl

import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

abstract class DefaultServiceGroup(
        private val name: String,
        private val templateName: String,
        private val serviceType: ServiceType,
        private val maxMemory: Int,
        private val maxPlayers: Int,
        private val minimumOnlineServiceCount: Int,
        private val maximumServiceCount: Int,
        private var maintenance: Boolean,
        private val static: Boolean
) : ICloudServiceGroup {

    override fun getName(): String = this.name

    override fun getTemplateName(): String = this.templateName

    override fun getServiceType(): ServiceType = this.serviceType

    override fun getMaxMemory(): Int = this.maxMemory

    override fun getMaxPlayers(): Int = this.maxPlayers

    override fun getMinimumOnlineServiceCount(): Int = this.minimumOnlineServiceCount

    override fun getMaximumOnlineServiceCount(): Int = this.maximumServiceCount

    override fun isInMaintenance(): Boolean = this.maintenance

    override fun isStatic(): Boolean = this.static

    override fun getPercentToStartNewService(): Int {
    }

    override fun getWrapperName(): String? {
    }

    override fun getModuleNamesToCopy(): List<String> {
    }

    override fun getAllServices(): List<ICloudService> {
    }
}