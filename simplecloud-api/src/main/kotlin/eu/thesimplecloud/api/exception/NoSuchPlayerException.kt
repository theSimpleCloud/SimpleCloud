package eu.thesimplecloud.api.exception

import java.lang.Exception

class NoSuchPlayerException(reason: String) : Exception(reason) {
}