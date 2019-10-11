package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperInfo
import java.io.File

class WrapperFileHandler : IFileHandler<IWrapperInfo> {

    val directory = File(DirectoryPaths.paths.wrappersPath)

    override fun save(value: IWrapperInfo) {
        JsonData.fromObject(value).saveAsFile(getFile(value))
    }

    override fun delete(value: IWrapperInfo) {
        getFile(value).delete()
    }

    override fun loadAll(): Set<IWrapperInfo> = directory.listFiles().mapNotNull { JsonData.fromJsonFile(it)?.getObject(DefaultWrapperInfo::class.java) }.toSet()


    fun getFile(wrapperInfo: IWrapperInfo): File = File(directory, wrapperInfo.getName() + ".json")
}