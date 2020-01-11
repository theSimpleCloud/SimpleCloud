package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.api.depedency.Dependency

data class CloudModuleFileContent(val name: String, val author: String, val mainClass: String, val moduleCopyType: ModuleCopyType, val repositories: List<String>, val dependencies: List<Dependency>) {
}