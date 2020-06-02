package eu.thesimplecloud.api.service.startconfiguration

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate

/**
 * Creates a new [ServiceStartConfiguration] with the default values of the specified service group
 */
class ServiceStartConfiguration(serviceGroup: ICloudServiceGroup) : IServiceStartConfiguration {

    /**
     * The name of the group
     */
    val groupName = serviceGroup.getName()

    /**
     * The memory amount in MB for the new service.
     */
    var maxMemory = serviceGroup.getMaxMemory()
        private set

    /**
     * The maximum amount of players for the new service.
     */
    var maxPlayers = serviceGroup.getMaxPlayers()
        private set

    /**
     * The template the new service shall use.
     */
    var template = serviceGroup.getTemplateName()
        private set

    /**
     * The number of the new service.
     * e.g: Lobby-2 -> 2 is the service number
     */
    var serviceNumber: Int? = null
        private set


    override fun getServiceGroupName(): String {
        return this.groupName
    }

    override fun setMaxMemory(memory: Int): ServiceStartConfiguration {
        require(memory >= 100) { "The specified memory must be at least 100" }
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(maxPlayers: Int): ServiceStartConfiguration {
        require(maxPlayers > 0) { "The specified amount of maxPlayers must be positive." }
        this.maxPlayers = maxPlayers
        return this
    }

    override fun setTemplate(template: ITemplate): ServiceStartConfiguration {
        this.template = template.getName()
        return this
    }

    override fun setServiceNumber(number: Int): ServiceStartConfiguration {
        require(number > 0) { "The specified number must be positive." }
        this.serviceNumber = number
        return this
    }


}