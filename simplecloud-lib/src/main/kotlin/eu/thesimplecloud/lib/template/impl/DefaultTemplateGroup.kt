package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.ITemplateGroup

class DefaultTemplateGroup(private val name: String) : ITemplateGroup {

    private val everyTemplate = DefaultTemplate(name, this)
    private val templates = ArrayList<ITemplate>()

    override fun getName(): String = this.name

    override fun getEveryTemplate(): ITemplate = this.everyTemplate

    override fun getTemplates(): List<ITemplate> = this.templates

    override fun addTemplate(template: ITemplate) {
        this.templates.add(template)
    }

    override fun removeTemplate(template: ITemplate) {
        this.templates.remove(template)
    }
}