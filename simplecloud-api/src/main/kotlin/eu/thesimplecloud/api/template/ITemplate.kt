package eu.thesimplecloud.api.template
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
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
    fun inheritedTemplates(): List<ITemplate> = getInheritedTemplateNames().mapNotNull { CloudAPI.instance.getTemplateManager().getTemplateByName(it) }

    /**
     * Adds the specified name to the list of all module names, this template shall copy in the plugins directory of a service.
     */
    fun addModuleNameToCopy(name: String)

    /**
     * Removes the specified name from the list of all module names, this template shall copy in the plugins directory of a service.
     */
    fun removeModuleNameToCopy(name: String)

    /**
     * Returns a list of names of all modules, this template shall copy in the plugins directory of a service.
     */
    fun getModuleNamesToCopy(): Set<String>
}