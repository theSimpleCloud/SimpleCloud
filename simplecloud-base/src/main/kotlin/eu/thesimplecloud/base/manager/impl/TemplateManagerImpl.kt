package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.api.network.packets.template.PacketIODeleteTemplate
import eu.thesimplecloud.api.network.packets.template.PacketIOUpdateTemplate
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager

class TemplateManagerImpl : DefaultTemplateManager() {

    private val templatesConfigLoader = TemplatesConfigLoader()

    override fun updateTemplate(template: ITemplate) {
        super.updateTemplate(template)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateTemplate(template))
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.getName().equals(template.getName(), true) }
        templateConfig.templates.add(template as DefaultTemplate)
        templatesConfigLoader.saveConfig(templateConfig)
    }

    override fun removeTemplate(name: String) {
        super.removeTemplate(name)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIODeleteTemplate(name))
        val templateConfig = this.templatesConfigLoader.loadConfig()
        templateConfig.templates.removeIf { it.getName().equals(name, true)}
        templatesConfigLoader.saveConfig(templateConfig)
    }

}