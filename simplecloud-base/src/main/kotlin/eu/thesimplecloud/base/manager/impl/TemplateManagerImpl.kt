package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager
import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader

class TemplateManagerImpl : DefaultTemplateManager() {

    private val templatesConfigLoader = TemplatesConfigLoader()

    override fun update(value: ITemplate, fromPacket: Boolean) {
        super.update(value, fromPacket)
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.getName().equals(value.getName(), true) }
        templateConfig.templates.add(value as DefaultTemplate)
        templatesConfigLoader.saveConfig(templateConfig)
    }

    override fun delete(value: ITemplate, fromPacket: Boolean) {
        super<DefaultTemplateManager>.delete(value, fromPacket)

        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.getName().equals(value.getName(), true)}
        templatesConfigLoader.saveConfig(templateConfig)
    }

}