package eu.thesimplecloud.api.client

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.utils.IConnectedCloudProcess

data class NetworkComponentReference(val cloudClientType: NetworkComponentType, val name: String) {

    companion object {
        @JvmStatic
        val MANAGER_COMPONENT = NetworkComponentReference(NetworkComponentType.MANAGER, "Manager")


    }

    fun getConnectedProcess(): IConnectedCloudProcess? {
        return when (cloudClientType) {
            NetworkComponentType.WRAPPER -> {
                CloudAPI.instance.getWrapperManager().getWrapperByName(name)?.obj
            }
            NetworkComponentType.SERVICE -> {
                CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
            }
            NetworkComponentType.MANAGER -> {
                IConnectedCloudProcess.MANAGER_CLOUD_PROCESS
            }
        }
    }

}