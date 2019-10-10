package eu.thesimplecloud.lib.template
import eu.thesimplecloud.lib.config.IConfigLoader
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File

interface ITemplate {

    /**
     * Returns the name of this template.
     */
    fun getName(): String

    /**
     * Returns the directory of this template.
     */
    fun getDirectory(): File = File(DirectoryPaths.paths.templatesPath + getName() + "/")

}