package eu.thesimplecloud.module.permission.player

import eu.thesimplecloud.api.player.IOfflineCloudPlayer


fun IOfflineCloudPlayer.getPermissionPlayer(classloader: ClassLoader = this::class.java.classLoader): IPermissionPlayer {
    return this.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(classloader)!!
}