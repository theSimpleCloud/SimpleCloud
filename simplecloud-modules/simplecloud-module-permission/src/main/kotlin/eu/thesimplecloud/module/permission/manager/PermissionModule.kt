package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.core.TestSynchronizedListObject

class PermissionModule : ICloudModule {
    override fun onEnable() {
        PermissionPool()
        CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList("test")!!.update(TestSynchronizedListObject(0, "xImMrManHDLPHDXD22"))
    }

    override fun onDisable() {
    }
}