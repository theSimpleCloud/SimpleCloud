package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate

data class DefaultTemplate(private val name: String) : ITemplate {

    private var inheritedTemplateNames = HashSet<String>()
    private var moduleNamesToCopy = HashSet<String>()

    override fun getName(): String = this.name

    override fun getInheritedTemplateNames(): Set<String> = this.inheritedTemplateNames

    override fun addInheritanceTemplate(template: ITemplate) {
        removeInheritanceTemplate(template)
        this.inheritedTemplateNames.add(template.getName())
    }

    override fun removeInheritanceTemplate(template: ITemplate) {
        this.inheritedTemplateNames.removeIf { it.equals(template.getName(), true) }
    }

    fun setInheritedTemplateNames(inheritedTemplateNames: Set<String>){
        this.inheritedTemplateNames = HashSet(inheritedTemplateNames)
    }

    override fun addModuleNameToCopy(name: String) {
        removeModuleNameToCopy(name)
        this.moduleNamesToCopy.add(name)
    }

    override fun removeModuleNameToCopy(name: String) {
        this.moduleNamesToCopy.removeIf { it.equals(name, true) }
    }

    override fun getModuleNamesToCopy(): Set<String> = this.moduleNamesToCopy

    fun setModuleNamesToCopy(moduleNamesToCopy: Set<String>){
        this.moduleNamesToCopy = HashSet(moduleNamesToCopy)
    }


}