package eu.thesimplecloud.base.manager.update.converter

class ConversionException(minorVersion: Int, cause: Throwable) :
    Exception("Conversion to version 2.${minorVersion} failed", cause) {
}