package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.ITemplateManager

class DefaultTemplateManager : ITemplateManager {

    private val templates = ArrayList<ITemplate>()

    override fun addTemplate(template: ITemplate) {
        this.templates.add(template)
    }

    override fun removeTemplate(name: String) {
        this.templates.remove(getTemplate(name))
    }

    override fun getAllTemplates(): List<ITemplate> = this.templates

    override fun clearCache() {
        this.templates.clear()
    }


}