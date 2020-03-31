package eu.thesimplecloud.launcher.exception

class CloudModuleException(message: String, ex: Exception?) : Exception(message, ex) {

    constructor(message: String) : this(message, null)

}