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

package eu.thesimplecloud.launcher.external.module

import com.google.common.collect.Maps
import eu.thesimplecloud.launcher.exception.module.IModuleHandler
import java.net.URL
import java.net.URLClassLoader

open class ModuleClassLoader(urls: Array<URL>, parent: ClassLoader, val moduleName: String, var moduleHandler: IModuleHandler?) : URLClassLoader(urls, parent) {

    @Volatile
    private var closed: Boolean = false
    protected val cachedClasses: MutableMap<String, Class<*>> = Maps.newConcurrentMap()

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }

    override fun findClass(name: String): Class<*> {
        if (closed) throw IllegalStateException("ModuleClassLoaders is already closed")
        return findClass0(name, true)
    }

    fun findClass0(name: String, checkGlobal: Boolean): Class<*> {
        val clazz = this.cachedClasses[name]
        if (clazz != null) return clazz
        val classByName = runCatching { super.findClass(name) }.getOrNull()
        if (classByName != null) {
            this.cachedClasses[name] = classByName
            return classByName
        }
        if (checkGlobal) {
            val otherModuleClass = this.moduleHandler?.findModuleClass(name)
            if (otherModuleClass != null) {
                this.cachedClasses[name] = otherModuleClass
                return otherModuleClass
            }
        }
        throw ClassNotFoundException(name)
    }

    override fun close() {
        super.close()
        this.closed = true
    }

    fun isClosed(): Boolean = this.closed

}