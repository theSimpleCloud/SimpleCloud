package eu.thesimplecloud.lib.servicegroup.impl

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

abstract class AbstractServiceGroup(
        private val name: String,
        private var templateName: String,
        private var maxMemory: Int,
        private var maxPlayers: Int,
        private var minimumOnlineServiceCount: Int,
        private var maximumOnlineServiceCount: Int,
        private var maintenance: Boolean,
        private val static: Boolean,
        private var percentToStartNewService: Int,
        private val wrapperName: String?,
        private var moduleNamesToCopy: List<String> = emptyList()
) : ICloudServiceGroup {

    override fun getName(): String = this.name

    override fun getTemplateName(): String = this.templateName

    override fun setTemplateName(name: String) {
        this.templateName = name
    }

    override fun getMaxMemory(): Int = this.maxMemory

    override fun setMaxMemory(memory: Int) {
        this.maxMemory = memory
    }

    override fun getMaxPlayers(): Int = this.maxPlayers

    override fun setMaxPlayers(maxPlayers: Int) {
        this.maxPlayers = maxPlayers
    }

    override fun getMinimumOnlineServiceCount(): Int = this.minimumOnlineServiceCount

    override fun setMinimumOnlineServiceCount(count: Int) {
        this.minimumOnlineServiceCount = count
    }

    override fun getMaximumOnlineServiceCount(): Int = this.maximumOnlineServiceCount

    override fun setMaximumOnlineServiceCount(count: Int) {
        this.maximumOnlineServiceCount = count
    }

    override fun isInMaintenance(): Boolean = this.maintenance

    override fun setMaintenance(maintenance: Boolean) {
        this.maintenance = maintenance
    }

    override fun isStatic(): Boolean = this.static

    override fun getPercentToStartNewService(): Int = this.percentToStartNewService

    override fun setPercentToStartNewService(percentage: Int) {
        this.percentToStartNewService = percentage
    }

    override fun getWrapperName(): String? = this.wrapperName

    override fun getModuleNamesToCopy(): List<String> = this.moduleNamesToCopy

    override fun setModuleNamesToCopy(list: List<String>) {
        this.moduleNamesToCopy = list
    }

}