/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.api.config.IFileHandler
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
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
        val list = directory.listFiles()?.map { JsonData.fromJsonFile(it)?.getObjectOrNull(clazz) } ?: emptyList<T>()
        return list.filterNotNull()
    }

    private fun getJsonFileForGroup(cloudServiceGroup: ICloudServiceGroup): File {
        return when (cloudServiceGroup.getServiceType()) {
            ServiceType.PROXY -> File(DirectoryPaths.paths.proxyGroupsPath, cloudServiceGroup.getName() + ".json")
            ServiceType.LOBBY -> File(DirectoryPaths.paths.lobbyGroupsPath, cloudServiceGroup.getName() + ".json")
            ServiceType.SERVER -> File(DirectoryPaths.paths.serverGroupsPath, cloudServiceGroup.getName() + ".json")
        }
    }
}