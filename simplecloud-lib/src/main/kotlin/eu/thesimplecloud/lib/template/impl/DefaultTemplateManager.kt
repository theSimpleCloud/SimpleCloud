package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplateGroup
import eu.thesimplecloud.lib.template.ITemplateManager

class DefaultTemplateManager : ITemplateManager {

    private val noneTemplate = DefaultTemplateGroup("")
    private val templateGroups = ArrayList<ITemplateGroup>()

    override fun getNoneTemplateGroup(): ITemplateGroup = this.noneTemplate

    override fun getTemplateGroups(): List<ITemplateGroup> = this.templateGroups

    override fun addTemplateGroup(templateGroup: ITemplateGroup) {
        this.templateGroups.add(templateGroup)
    }

    override fun deleteTemplateGroup(templateGroup: ITemplateGroup) {
        this.templateGroups.remove(templateGroup)
    }
}