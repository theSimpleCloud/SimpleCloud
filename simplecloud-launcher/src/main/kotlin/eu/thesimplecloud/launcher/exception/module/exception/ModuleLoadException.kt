package eu.thesimplecloud.launcher.exception.module.exception

class ModuleLoadException(moduleName: String, ex: Exception?) : Exception("An error occurred while loading module: $moduleName", ex) {

    constructor(moduleName: String) : this(moduleName, null)

}