package eu.thesimplecloud.lib.template
import eu.thesimplecloud.lib.CloudLib
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

    /**
     * Returns the names of all templates this group inherits from.
     */
    fun getInheritedTemplateNames(): Set<String>

    /**
     * Adds a template as inherited
     */
    fun addInheritanceTemplate(template: ITemplate)

    /**
     * Removes a template from the list of inherited templates
     */
    fun removeInheritanceTemplate(template: ITemplate)

    /**
     * Returns all templates this group inherits from.
     */
    fun inheritedTemplates(): List<ITemplate> = getInheritedTemplateNames().mapNotNull { CloudLib.instance.getTemplateManager().getTemplate(it) }
}