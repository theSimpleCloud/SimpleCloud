package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.api.depedency.Dependency
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import java.io.File

abstract class AbstractUpdater(
        private val groupId: String,
        private val artifactId: String,
        protected val updateFile: File
) : IUpdater {

    private var versionToInstall: String? = null

    private var wasVersionToInstallCalled: Boolean = false

    override fun getVersionToInstall(): String? {
        if (!wasVersionToInstallCalled) {
            this.wasVersionToInstallCalled = true
            this.versionToInstall = DependencyLoader().getLatestVersionOfDependencyFromWeb(groupId, artifactId, getRepositoryURL())
        }
        return this.versionToInstall
    }

    override fun downloadJarsForUpdate() {
        val latestVersion = getVersionToInstall()
                ?: throw RuntimeException("Cannot perform update. Is the server down? (repo: ${getRepositoryURL()})")
        val dependency = Dependency(groupId, artifactId, latestVersion)
        dependency.download(getRepositoryURL(), updateFile)
    }


}