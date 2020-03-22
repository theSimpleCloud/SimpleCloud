package eu.thesimplecloud.module.sign.lib

import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.TemplateLocation
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject

class SignModuleConfig(
        val cloudSigns: MutableList<CloudSign>,
        val groupToLayout: GroupToLayout,
        val signLayouts: MutableList<SignLayout>
) : ISynchronizedObject {

    override fun getName(): String = "simplecloud-module-sign-config"

    fun getCloudSignByLocation(templateLocation: TemplateLocation): CloudSign? {
        return this.cloudSigns.firstOrNull { it.templateLocation == templateLocation }
    }

    fun getSignLayoutByGroupName(groupName: String): SignLayout? {
        return this.groupToLayout.getLayoutByGroupName(groupName)
    }

    fun getSignLayoutByName(name: String): SignLayout? {
        return this.signLayouts.firstOrNull { it.name == name }
    }

    fun getSearchingLayout(): SignLayout {
        return this.signLayouts.first { it.name == "SEARCHING" }
    }

    fun getStartingLayout(): SignLayout {
        return this.signLayouts.first { it.name == "STARTING" }
    }

    fun getMaintenanceLayout(): SignLayout {
        return this.signLayouts.first { it.name == "MAINTENANCE" }
    }

    companion object {
        lateinit var INSTANCE: SignModuleConfig
    }
}