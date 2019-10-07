package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.dependency.Dependency

class ApplicationFileContent(val mainClass: String, val repositories: List<String>, val dependencies: List<Dependency>) {
}