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
import java.util.concurrent.ConcurrentHashMap

data class DefaultTemplate(private val name: String) : ITemplate {

    @Volatile private var inheritedTemplateNames: MutableSet<String> = ConcurrentHashMap.newKeySet<String>()
    @Volatile private var moduleNamesToCopy: MutableSet<String> = ConcurrentHashMap.newKeySet<String>()

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
        this.inheritedTemplateNames = ConcurrentHashMap.newKeySet()
        this.inheritedTemplateNames.addAll(inheritedTemplateNames)
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
        this.moduleNamesToCopy = ConcurrentHashMap.newKeySet()
        this.moduleNamesToCopy.addAll(moduleNamesToCopy)
    }


}