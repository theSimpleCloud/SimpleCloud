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