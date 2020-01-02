package eu.thesimplecloud.launcher.module

import eu.thesimplecloud.api.depedency.Dependency

data class CloudModuleFileContent(val mainClass: String, val repositories: List<String>, val dependencies: List<Dependency>) {
}