package eu.thesimplecloud.plugin

import eu.thesimplecloud.api.player.ICloudPlayerManager
import kotlin.reflect.KClass


interface ICloudServicePlugin {

    fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager>

    fun shutdown()

}