/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.module.rest.defaultcontroller.template

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.module.rest.annotation.*
import eu.thesimplecloud.module.rest.controller.IController

/**
 * Created by IntelliJ IDEA.
 * Date: 06.10.2020
 * Time: 19:59
 * @author Frederick Baier
 */
@RestController("cloud/template/")
class TemplateController : IController {

    @RequestMapping(RequestType.GET, "", "web.cloud.template.get.all")
    fun handleGetAllTemplates(): List<ITemplate> {
        return CloudAPI.instance.getTemplateManager().getAllCachedObjects()
    }

    @RequestMapping(RequestType.GET, "name/:name/", "web.cloud.template.get.one")
    fun handleGetOneTemplates(@RequestPathParam("name") name: String): ITemplate? {
        return CloudAPI.instance.getTemplateManager().getTemplateByName(name)
    }

    @RequestMapping(RequestType.POST, "", "web.cloud.template.create")
    fun handleCreateTemplate(@RequestBody template: DefaultTemplate): Boolean {
        if (doesTemplateExist(template.getName())) return false
        CloudAPI.instance.getTemplateManager().update(template)
        return true
    }

    @RequestMapping(RequestType.PUT, "", "web.cloud.template.update")
    fun handleUpdateTemplate(@RequestBody template: DefaultTemplate): Boolean {
        if (!doesTemplateExist(template.getName())) return false
        CloudAPI.instance.getTemplateManager().update(template)
        return true
    }

    @RequestMapping(RequestType.DELETE, "name/:name", "web.cloud.template.delete")
    fun handleDeleteTemplate(@RequestPathParam("name") name: String): Boolean {
        if (!doesTemplateExist(name)) return false
        CloudAPI.instance.getTemplateManager().deleteTemplate(name)
        return true
    }

    private fun doesTemplateExist(name: String): Boolean {
        return CloudAPI.instance.getTemplateManager().getTemplateByName(name) != null
    }


}