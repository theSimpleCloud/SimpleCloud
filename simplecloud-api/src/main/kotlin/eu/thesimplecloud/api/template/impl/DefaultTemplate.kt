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

package eu.thesimplecloud.api.template.impl

import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.template.ITemplateUpdater
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.JsonLibExclude

data class DefaultTemplate(private val name: String) : ITemplate {

    @Volatile
    private var inheritedTemplateNames: Set<String> = emptySet()
    @Volatile
    private var moduleNamesToCopy: Set<String> = emptySet()

    @PacketExclude
    @JsonLibExclude
    @Volatile
    private var templateUpdater: DefaultTemplateUpdater? = DefaultTemplateUpdater(this)

    override fun getName(): String = this.name

    override fun getInheritedTemplateNames(): Set<String> = this.inheritedTemplateNames

    override fun addInheritanceTemplate(template: ITemplate) {
        getUpdater().addInheritanceTemplate(template)
    }

    override fun removeInheritanceTemplate(template: ITemplate) {
        getUpdater().removeInheritanceTemplate(template)
    }

    override fun addModuleNameToCopy(name: String) {
        getUpdater().addModuleNameToCopy(name)
    }

    override fun removeModuleNameToCopy(name: String) {
        getUpdater().removeModuleNameToCopy(name)
    }

    override fun getModuleNamesToCopy(): Set<String> = this.moduleNamesToCopy

    override fun getUpdater(): ITemplateUpdater {
        if (this.templateUpdater == null) {
            this.templateUpdater = DefaultTemplateUpdater(this)
        }
        return this.templateUpdater!!
    }

    override fun applyValuesFromUpdater(updater: ITemplateUpdater) {
        this.inheritedTemplateNames = HashSet(updater.getInheritedTemplateNames())
        this.moduleNamesToCopy =  HashSet(updater.getModuleNamesToCopy())
    }


}