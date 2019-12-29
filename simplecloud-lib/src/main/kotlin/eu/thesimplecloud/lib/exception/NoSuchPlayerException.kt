package eu.thesimplecloud.lib.exception

import java.lang.Exception

class NoSuchPlayerException(reason: String) : Exception(reason) {
}