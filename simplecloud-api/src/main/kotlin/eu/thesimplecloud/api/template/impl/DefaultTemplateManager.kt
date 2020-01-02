package eu.thesimplecloud.api.template.impl

import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.ITemplateManager
import java.util.*

open class DefaultTemplateManager : ITemplateManager {

    private val templates = Collections.synchronizedCollection(HashSet<ITemplate>())

    override fun updateTemplate(template: ITemplate) {
        val cachedTemplate = getTemplate(template.getName())
        if (cachedTemplate == null){
            this.templates.add(template)
            return
        }
        cachedTemplate as DefaultTemplate
        cachedTemplate.setInheritedTemplateNames(template.getInheritedTemplateNames())
        cachedTemplate.setModuleNamesToCopy(template.getModuleNamesToCopy())
    }

    override fun removeTemplate(name: String) {
        this.templates.remove(getTemplate(name))
    }

    override fun getAllTemplates(): Collection<ITemplate> = this.templates

    override fun clearCache() {
        this.templates.clear()
    }


}