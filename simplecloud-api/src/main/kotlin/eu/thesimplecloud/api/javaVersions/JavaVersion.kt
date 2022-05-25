package eu.thesimplecloud.api.javaVersions


class JavaVersion(
    var java8: String = "java",
    var java11: String = "java",
    var java16: String = "java",
    var java17: String = "java",
    var java18: String = "java"
) {

    companion object {
        lateinit var paths: JavaVersion
    }

}