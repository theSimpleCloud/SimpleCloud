package eu.thesimplecloud.api.javaVersions


class JavaVersion(
    var java8: String? = null,
    var java11: String? = null,
    var java16: String? = null,
    var java17: String? = null,
    var java18: String? = null,
) {

    companion object {
        lateinit var paths: JavaVersion
    }

}