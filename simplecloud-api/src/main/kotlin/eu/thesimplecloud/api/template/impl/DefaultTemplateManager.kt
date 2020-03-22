package eu.thesimplecloud.api.template.impl

import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.ITemplateManager
import java.util.concurrent.CopyOnWriteArrayList

open class DefaultTemplateManager : ITemplateManager {

    private val templates = CopyOnWriteArrayList<ITemplate>()

    override fun updateTemplate(template: ITemplate) {
        val cachedTemplate = getTemplateByName(template.getName())
        if (cachedTemplate == null){
            this.templates.add(template)
            return
        }
        cachedTemplate as DefaultTemplate
        cachedTemplate.setInheritedTemplateNames(template.getInheritedTemplateNames())
        cachedTemplate.setModuleNamesToCopy(template.getModuleNamesToCopy())
    }

    override fun removeTemplate(name: String) {
        this.templates.remove(getTemplateByName(name))
    }

    override fun getAllTemplates(): Collection<ITemplate> = this.templates

    override fun clearCache() {
        this.templates.clear()
    }


}