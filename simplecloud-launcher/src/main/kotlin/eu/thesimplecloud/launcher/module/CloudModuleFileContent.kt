package eu.thesimplecloud.launcher.module

import eu.thesimplecloud.launcher.dependency.Dependency

class CloudModuleFileContent(val mainClass: String, val repositories: List<String>, val dependencies: List<Dependency>) {
}