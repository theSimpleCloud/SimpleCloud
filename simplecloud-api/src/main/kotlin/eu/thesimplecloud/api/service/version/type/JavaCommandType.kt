package eu.thesimplecloud.api.service.version.type

import eu.thesimplecloud.api.javaVersions.JavaVersion

enum class JavaCommandType(val javaVersion: String) {

    JAVA_8(JavaVersion.paths.java18 ?: "java"),
    JAVA_11(JavaVersion.paths.java17 ?: "java"),
    JAVA_16(JavaVersion.paths.java16 ?: "java"),
    JAVA_17(JavaVersion.paths.java11 ?: "java"),
    JAVA_18(JavaVersion.paths.java8 ?: "java");

}