package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.api.config.IFileHandler
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import java.io.File

class WrapperFileHandler : IFileHandler<IWrapperInfo> {

    private val directory = File(DirectoryPaths.paths.wrappersPath)

    override fun save(value: IWrapperInfo) {
        JsonData.fromObject(value).saveAsFile(getFile(value))
    }

    override fun delete(value: IWrapperInfo) {
        getFile(value).delete()
    }

    override fun loadAll(): Set<IWrapperInfo> = directory.listFiles().mapNotNull { JsonData.fromJsonFile(it)?.getObject(DefaultWrapperInfo::class.java) }.toSet()


    fun getFile(wrapperInfo: IWrapperInfo): File = File(directory, wrapperInfo.getName() + ".json")
}