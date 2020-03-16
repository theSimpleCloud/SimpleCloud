package eu.thesimplecloud.module.permission.player

import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.permission.entity.PermissionEntity
import java.util.*

class PermissionPlayer(
        private val name: String,
        private val uniqueId: UUID,
        private val permissionGroupName: String
) : PermissionEntity(), IPermissionPlayer {

    override fun getName(): String = this.name

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getPermissionGroupName(): String = this.permissionGroupName

    override fun update(): ICommunicationPromise<Unit> {
        return getCloudPlayer().then {
            it.setProperty(PROPERTY_NAME, Property(this))
            it.update()
        }
    }

    companion object {
        const val PROPERTY_NAME = "simplecloud-module-permission-player"
    }

}