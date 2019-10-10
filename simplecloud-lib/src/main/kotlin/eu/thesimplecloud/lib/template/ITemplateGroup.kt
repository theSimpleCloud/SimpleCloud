package eu.thesimplecloud.lib.template

import java.io.File

interface ITemplateGroup {

    /**
     * Returns the name of this template group
     */
    fun getName(): String

    /**
     * Returns the every template of this template group
     */
    fun getEveryTemplate(): ITemplate

    /**
     * Returns all templates of this template group excluding the every template
     */
    fun getTemplates(): List<ITemplate>

    /**
     * Returns the first template found by the specified name
     */
    fun getTemplateByName(name: String) = getTemplates().firstOrNull { it.getName().equals(name, true) }

    /**
     * Adds the specified template to this group
     */
    fun addTemplate(template: ITemplate)

    /**
     * Removes the specified template to this group
     */
    fun removeTemplate(template: ITemplate)


}