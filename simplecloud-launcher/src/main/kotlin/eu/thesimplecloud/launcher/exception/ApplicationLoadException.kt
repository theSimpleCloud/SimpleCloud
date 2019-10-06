package eu.thesimplecloud.launcher.exception

import java.lang.Exception

class ApplicationLoadException(message: String, ex: Exception?) : Exception(message, ex) {

    constructor(message: String) : this(message, null)

}