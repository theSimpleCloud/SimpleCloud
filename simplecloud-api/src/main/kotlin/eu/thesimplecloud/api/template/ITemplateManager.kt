package eu.thesimplecloud.api.template

import eu.thesimplecloud.api.cachelist.ICacheList


interface ITemplateManager : ICacheList<ITemplate> {

    /**
     * Removes the template found by the specified name
     */
    fun deleteTemplate(name: String)

    /**
     * Returns the first template found by the specified name
     */
    fun getTemplateByName(name: String): ITemplate? = getAllCachedObjects().firstOrNull { it.getName().equals(name, true) }

}