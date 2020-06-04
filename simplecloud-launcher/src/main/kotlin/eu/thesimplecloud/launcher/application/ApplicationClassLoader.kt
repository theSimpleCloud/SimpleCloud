/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.exception.module.IModuleHandler
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader
import java.net.URL

class ApplicationClassLoader(
        urls: Array<URL>,
        parent: ClassLoader,
        applicationName: String,
        moduleHandler: IModuleHandler
) : ModuleClassLoader(urls, parent, "Cloud-Application-$applicationName", moduleHandler) {

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }

    fun clearCachedClasses() {
        this.cachedClasses.clear()
    }

    override fun findClass(name: String): Class<*> {
        return super.findClass(name)
    }

    fun hasCached(clazz: Class<*>): Boolean {
        return this.cachedClasses.values.contains(clazz)
    }

    fun setCachedClass(clazz: Class<*>) {
        this.cachedClasses[clazz.name] = clazz
    }

    fun isThisClassLoader(clazz: Class<*>): Boolean {
        return clazz.classLoader === this
    }

}