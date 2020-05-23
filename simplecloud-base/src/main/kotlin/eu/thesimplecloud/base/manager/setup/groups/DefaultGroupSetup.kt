package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

open class DefaultGroupSetup {

    /**
     * Creates a template and returns its name
     */
    fun createTemplate(templateName: String, groupName: String): String? {
        if (templateName.equals("create", true)) {
            if (CloudAPI.instance.getTemplateManager().getTemplateByName(groupName) == null) {
                val template = DefaultTemplate(groupName)
                CloudAPI.instance.getTemplateManager().update(template)
                template.getDirectory().mkdirs()
            }
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.template.created", "Template %TEMPLATE%", groupName, " created.")
            return groupName
        }
        if (CloudAPI.instance.getTemplateManager().getTemplateByName(templateName) == null) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.template.not-exist", "The specified template does not exist.")
            return null
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.template.success", "Template set.")
        return templateName
    }

}