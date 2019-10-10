package eu.thesimplecloud.lib.template


interface ITemplateManager {

    /**
     * Adds the specified template
     */
    fun addTemplate(template: ITemplate)

    /**
     * Removes the template found by the specified name
     */
    fun removeTemplate(name: String)

    /**
     * Returns a list containing all registered templates
     */
    fun getAllTemplates() : List<ITemplate>

    /**
     * Returns the first template found by the specified name
     */
    fun getTemplate(name: String): ITemplate? = getAllTemplates().firstOrNull { it.getName().equals(name, true) }

    /**
     * Clears the cache
     */
    fun clearCache()

}