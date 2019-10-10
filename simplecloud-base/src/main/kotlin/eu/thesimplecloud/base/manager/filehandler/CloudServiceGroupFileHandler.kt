package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultServerGroup
import java.io.File

class CloudServiceGroupFileHandler : IFileHandler<ICloudServiceGroup> {


    override fun save(value: ICloudServiceGroup) {
        val file = getJsonFileForGroup(value)
        JsonData.fromObject(value).saveAsFile(file)
    }

    override fun delete(value: ICloudServiceGroup) {
        getJsonFileForGroup(value).delete()
    }

    override fun loadAll(): Set<ICloudServiceGroup> {
        val proxyGroups = getAllFilesInDirectoryParsedAs(File(DirectoryPaths.paths.proxyGroupsPath), DefaultProxyGroup::class.java)
        val lobbyGroups = getAllFilesInDirectoryParsedAs(File(DirectoryPaths.paths.lobbyGroupsPath), DefaultLobbyGroup::class.java)
        val serverGroups = getAllFilesInDirectoryParsedAs(File(DirectoryPaths.paths.serverGroupsPath), DefaultServerGroup::class.java)
        return proxyGroups.union(lobbyGroups).union(serverGroups)
    }

    private fun <T : Any> getAllFilesInDirectoryParsedAs(directory: File, clazz: Class<T>) : List<T> {
        val list = directory.listFiles()?.map { JsonData.fromJsonFile(it).getObject(clazz) } ?: emptyList<T>()
        return list.filterNotNull()
    }

    private fun getJsonFileForGroup(cloudServiceGroup: ICloudServiceGroup): File {
        val file = when (cloudServiceGroup.getServiceType()) {
            ServiceType.PROXY -> File(DirectoryPaths.paths.proxyGroupsPath, cloudServiceGroup.getName() + ".json")
            ServiceType.LOBBY -> File(DirectoryPaths.paths.lobbyGroupsPath, cloudServiceGroup.getName() + ".json")
            ServiceType.SERVER -> File(DirectoryPaths.paths.serverGroupsPath, cloudServiceGroup.getName() + ".json")
        }
        return file
    }
}