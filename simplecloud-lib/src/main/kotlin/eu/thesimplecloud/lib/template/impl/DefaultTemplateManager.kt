package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.ITemplateManager

open class DefaultTemplateManager : ITemplateManager {

    private val templates = HashSet<ITemplate>()

    override fun updateTemplate(template: ITemplate) {
        val cachedTemplate = getTemplate(template.getName())
        if (cachedTemplate == null){
            this.templates.add(template)
            return
        }
        cachedTemplate as DefaultTemplate
        cachedTemplate.setInheritedTemplateNames(template.getInheritedTemplateNames())
    }

    override fun removeTemplate(name: String) {
        this.templates.remove(getTemplate(name))
    }

    override fun getAllTemplates(): Set<ITemplate> = this.templates

    override fun clearCache() {
        this.templates.clear()
    }


}