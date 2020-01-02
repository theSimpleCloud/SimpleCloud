package eu.thesimplecloud.api.servicegroup.grouptype

import eu.thesimplecloud.api.service.ServiceType

interface ICloudLobbyGroup : ICloudServerGroup {

    /**
     * Returns the priority of this lobby group.
     * When a player joins the lobby groups permissions with higher priority will be checked first.
     */
    fun getPriority(): Int

    /**
     * Sets the priority of this lobby group.
     */
    fun setPriority(priority: Int)

    /**
     * Returns the permission a player needs to join a service of this group or null if there is no permission set.
     */
    fun getPermission(): String?

    /**
     * Sets the permission a player needs to join a service of this group
     */
    fun setPermission(permission: String)

    override fun getServiceType(): ServiceType = ServiceType.LOBBY

}