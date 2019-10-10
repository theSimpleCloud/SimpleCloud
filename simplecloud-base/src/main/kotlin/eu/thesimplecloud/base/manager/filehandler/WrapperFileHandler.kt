package eu.thesimplecloud.base.manager.filehandler

import eu.thesimplecloud.lib.config.IFileHandler
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import java.io.File
import java.util.logging.FileHandler

class WrapperFileHandler : IFileHandler<IWrapperInfo> {

    val directory = File(DirectoryPaths.paths.wrappersPath)

    override fun save(value: IWrapperInfo) {
    }

    override fun delete(value: IWrapperInfo) {
    }

    override fun loadAll(): Set<IWrapperInfo> {
    }
}