package eu.thesimplecloud.loader.dependency

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import eu.thesimplecloud.runner.dependency.CloudDependency
import eu.thesimplecloud.runner.dependency.SimpleDependencyDownloader
import org.eclipse.aether.artifact.DefaultArtifact
import java.io.IOException

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.06.2021
 * Time: 21:18
 */
class AdvancedDependencyDownloader(repositories: List<String>) : SimpleDependencyDownloader(repositories) {

    fun downloadFiles(dependency: AdvancedCloudDependency) {
        if (dependency.getDownloadedFile().exists()) return
        this.repositories.forEach { repoUrl ->
            try {
                downloadAnyways(dependency, repoUrl)
                return
            } catch (e: Exception) {
                //ignore because the repository was wrong and another repository will be correct
            }
        }

        throw IllegalArgumentException("No valid repository was found for ${dependency.getName()} repos: $repositories")
    }

    @Throws(IOException::class)
    private fun downloadAnyways(dependency: AdvancedCloudDependency, repoUrl: String) {
        dependency.download(repoUrl)
        resolveDependenciesAndSaveToInfoFile(dependency, repoUrl)
    }

    private fun resolveDependenciesAndSaveToInfoFile(dependency: AdvancedCloudDependency, repoUrl: String) {
        val aetherArtifact = DefaultArtifact("${dependency.groupId}:${dependency.artifactId}:${dependency.version}")
        val dependencies = DependencyResolver(repoUrl, aetherArtifact).collectDependencies()
        val cloudDependencies = dependencies.map { it.artifact }
            .map { CloudDependency(it.groupId, it.artifactId, it.version) }
        JsonLib.fromObject(cloudDependencies).saveAsFile(dependency.getDownloadedInfoFile())
    }

}