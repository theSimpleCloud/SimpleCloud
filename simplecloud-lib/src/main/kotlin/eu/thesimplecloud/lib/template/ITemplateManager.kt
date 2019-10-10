package eu.thesimplecloud.lib.template


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

    /**
     *  Returns the first template found by the specified name
     */
    fun getTemplateByName(name: String) : ITemplate? = getTemplateGroups().map { it.getTemplates() }.flatten().firstOrNull { it.getName().equals(name, true) }


}