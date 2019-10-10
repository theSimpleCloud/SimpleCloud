package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.ITemplateGroup

data class DefaultTemplate(private val name: String, private val templateGroup: ITemplateGroup) : ITemplate {

    override fun getName(): String = this.name

    override fun getTemplateGroup(): ITemplateGroup = this.templateGroup
}