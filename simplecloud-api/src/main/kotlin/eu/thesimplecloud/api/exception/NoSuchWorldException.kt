package eu.thesimplecloud.api.exception

import java.lang.Exception

class NoSuchWorldException(reason: String) : Exception(reason) {
}