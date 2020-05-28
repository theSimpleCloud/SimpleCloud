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