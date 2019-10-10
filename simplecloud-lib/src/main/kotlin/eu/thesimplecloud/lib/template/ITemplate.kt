package eu.thesimplecloud.lib.template

import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import java.io.File

interface ITemplate {

    /**
     * Returns the name of this template.
     */
    fun getName(): String

    /**
     * Returns the [ITemplateGroup] this template belongs to
     */
    fun getTemplateGroup(): ITemplateGroup

    /**
     * Returns the directory of this template.
     */
    fun getDirectory(): File = DirectoryPaths.paths.tem getTemplateGroup().getName()

}