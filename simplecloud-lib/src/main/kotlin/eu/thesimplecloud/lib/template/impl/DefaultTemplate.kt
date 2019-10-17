package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate

data class DefaultTemplate(private val name: String) : ITemplate {

    private val inheritedTemplateNames: MutableSet<String> = HashSet()

    override fun getName(): String = this.name

    override fun getInheritedTemplateNames(): Set<String> = this.inheritedTemplateNames

    override fun addInheritanceTemplate(template: ITemplate) {
        this.inheritedTemplateNames.add(template.getName().toLowerCase())
    }

    override fun removeInheritanceTemplate(template: ITemplate) {
        this.inheritedTemplateNames.remove(template.getName().toLowerCase())
    }

    fun setInheritedTemplateNames(inheritedTemplateNames: Set<String>){
        this.inheritedTemplateNames.clear()
        this.inheritedTemplateNames.addAll(inheritedTemplateNames)
    }

}