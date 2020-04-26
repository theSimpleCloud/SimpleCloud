package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.api.depedency.Dependency

data class ModuleFileContent(
        val name: String,
        val author: String,
        val mainClass: String,
        val moduleCopyType: ModuleCopyType,
        val repositories: List<String>,
        val dependencies: List<Dependency>,
        val depend: List<String>
) {

    fun isDependencyOf(moduleFileContent: ModuleFileContent): Boolean {
        return moduleFileContent.depend.contains(name)
    }

    fun dependsFrom(dependencyFileContent: ModuleFileContent): Boolean {
        return this.depend.contains(dependencyFileContent.name)
    }

}