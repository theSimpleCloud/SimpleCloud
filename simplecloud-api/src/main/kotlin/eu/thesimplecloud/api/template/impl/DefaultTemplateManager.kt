package eu.thesimplecloud.api.template.impl

import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.ITemplateManager

open class DefaultTemplateManager : AbstractCacheList<ITemplate>(), ITemplateManager {

    private val updater = object : ICacheObjectUpdater<ITemplate> {
        override fun getIdentificationName(): String {
            return "template-cache"
        }

        override fun getCachedObjectByUpdateValue(value: ITemplate): ITemplate? {
            return getTemplateByName(value.getName())
        }

        override fun determineEventsToCall(updateValue: ITemplate, cachedValue: ITemplate?): List<IEvent> {
            return emptyList()
        }

        override fun mergeUpdateValue(updateValue: ITemplate, cachedValue: ITemplate) {
            cachedValue as DefaultTemplate
            cachedValue.setInheritedTemplateNames(updateValue.getInheritedTemplateNames())
            cachedValue.setModuleNamesToCopy(updateValue.getModuleNamesToCopy())
        }

        override fun addNewValue(value: ITemplate) {
            values.add(value)
        }

    }

    override fun deleteTemplate(name: String) {
        getTemplateByName(name)?.let { this.delete(it) }
    }

    override fun getUpdater(): ICacheObjectUpdater<ITemplate> {
        return this.updater
    }

}