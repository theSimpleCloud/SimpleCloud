package eu.thesimplecloud.lib.template

import eu.thesimplecloud.lib.template.impl.DefaultTemplateGroup

interface ITemplateManager {

    /**
     * Returns the none template group
     */
    fun getNoneTemplateGroup() : ITemplateGroup

    /**
     * Returns a list containing all registered [ITemplateGroup]s except the [getNoneTemplateGroup]
     */
    fun getTemplateGroups(): List<ITemplateGroup>

    /**
     * Adds a [ITemplateGroup]
     */
    fun addTemplateGroup(templateGroup: ITemplateGroup)

    /**
     * Deletes a [ITemplateGroup]
     */
    fun deleteTemplateGroup(templateGroup: ITemplateGroup)


}