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

package eu.thesimplecloud.api.template

import eu.thesimplecloud.api.CloudAPI

/**
 * Created by IntelliJ IDEA.
 * Date: 23.01.2021
 * Time: 22:19
 * @author Frederick Baier
 */
interface ITemplateVariables {

    /**
     * Returns the names of all templates this group inherits from.
     */
    fun getInheritedTemplateNames(): Set<String>

    /**
     * Adds a template as inherited
     */
    fun addInheritanceTemplate(template: ITemplate)

    /**
     * Removes a template from the list of inherited templates
     */
    fun removeInheritanceTemplate(template: ITemplate)

    /**
     * Returns all templates this group inherits from.
     */
    fun inheritedTemplates(): List<ITemplate> =
        getInheritedTemplateNames().mapNotNull { CloudAPI.instance.getTemplateManager().getTemplateByName(it) }

    /**
     * Adds the specified name to the list of all module names, this template shall copy in the plugins directory of a service.
     */
    fun addModuleNameToCopy(name: String)

    /**
     * Removes the specified name from the list of all module names, this template shall copy in the plugins directory of a service.
     */
    fun removeModuleNameToCopy(name: String)

    /**
     * Returns a list of names of all modules, this template shall copy in the plugins directory of a service.
     */
    fun getModuleNamesToCopy(): Set<String>

}