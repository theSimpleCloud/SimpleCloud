package eu.thesimplecloud.api.javaVersions


class JavaVersion(
    var versions: MutableMap<String, String> = mutableMapOf()
) {

    companion object {
        lateinit var paths: JavaVersion
    }

}