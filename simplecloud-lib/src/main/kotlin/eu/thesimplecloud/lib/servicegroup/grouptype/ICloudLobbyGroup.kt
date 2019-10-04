package eu.thesimplecloud.lib.servicegroup.grouptype

interface ICloudLobbyGroup : ICloudServerGroup {

    /**
     * Returns the priority of this lobby group.
     * When a player joins the lobby groups permissions with higher priority will be checked first.
     */
    fun getPriority(): Int

    /**
     * Returns the permission a player needs to join a service of this group
     */
    fun getPermission(): String

}