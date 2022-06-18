package eu.thesimplecloud.launcher.config.java


class JavaVersion(
    var versions: MutableMap<String, String> = mutableMapOf()
) {

    companion object {
        lateinit var paths: JavaVersion
    }

}