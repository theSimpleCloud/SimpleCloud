package eu.thesimplecloud.module.permission.player

import eu.thesimplecloud.api.player.IOfflineCloudPlayer


fun IOfflineCloudPlayer.getPermissionPlayer(): IPermissionPlayer?  {
    return this.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue()
}