package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplateManager

class TemplateManagerImpl : DefaultTemplateManager() {

    private val templatesConfigLoader = TemplatesConfigLoader()

    override fun updateTemplate(template: ITemplate) {
        super.updateTemplate(template)
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it == template }
        templateConfig.templates.add(template as DefaultTemplate)
        templatesConfigLoader.saveConfig(templateConfig)
    }

    override fun removeTemplate(name: String) {
        super.removeTemplate(name)
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.getName().equals(name, true)}
        templatesConfigLoader.saveConfig(templateConfig)
    }

}