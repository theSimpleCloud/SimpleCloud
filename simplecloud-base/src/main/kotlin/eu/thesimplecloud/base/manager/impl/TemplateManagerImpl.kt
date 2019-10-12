package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplateManager

class TemplateManagerImpl : DefaultTemplateManager() {

    private val templatesConfigLoader = TemplatesConfigLoader()

    override fun addTemplate(template: ITemplate) {
        super.addTemplate(template)
        val templateConfig = this.templatesConfigLoader.loadConfig()
        if (templateConfig.templates.contains(template.getName()))
            return
        templateConfig.templates.add(template.getName())
        templatesConfigLoader.saveConfig(templateConfig)
    }

    override fun removeTemplate(name: String) {
        super.removeTemplate(name)
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.equals(name , true) }
        templatesConfigLoader.saveConfig(templateConfig)
    }

}