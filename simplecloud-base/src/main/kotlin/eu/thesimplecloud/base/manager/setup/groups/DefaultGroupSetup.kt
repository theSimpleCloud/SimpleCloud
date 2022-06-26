/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.launcher.startup.Launcher

open class DefaultGroupSetup {

    protected var permission: String? = null

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
            Launcher.instance.consoleSender.sendPropertyInSetup(
                "manager.setup.service-group.question.template.created",
                groupName
            )
            return groupName
        }
        if (CloudAPI.instance.getTemplateManager().getTemplateByName(templateName) == null) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.template.not-exist")
            return null
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.template.success")
        return templateName
    }

    protected fun handlePermission(permission: String) {
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.permission.success")
        if (permission.isNotBlank())
            this.permission = permission
    }

}