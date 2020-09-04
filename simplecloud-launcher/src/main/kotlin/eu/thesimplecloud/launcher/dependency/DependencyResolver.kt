/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.dependency

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactDescriptorRequest
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import java.io.File
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 16.07.2020
 * Time: 16:22
 * @author Frederick Baier
 */
class DependencyResolver(repositoryURL: String, private val dependency: DefaultArtifact) {

    private val locator: DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator()
    private val system: RepositorySystem = newRepositorySystem(locator)
    private val session: RepositorySystemSession = newSession(system)

    private val repository = RemoteRepository.Builder(UUID.randomUUID().toString(), "default", repositoryURL).build()

    fun collectDependencies(): List<Dependency> {
        return collectDependencies(dependency).filter { !it.isOptional }.filter { it.scope == "compile" }
    }

    fun determineLatestVersion(): String {
        val request = VersionRangeRequest(dependency, listOf(repository), null)
        return system.resolveVersionRange(session, request).highestVersion.toString()
    }

    private fun collectDependencies(artifact: DefaultArtifact): MutableList<Dependency> {
        val request = ArtifactDescriptorRequest()
        request.artifact = artifact
        request.repositories = listOf(repository)
        return system.readArtifactDescriptor(session, request).dependencies
    }

    private fun newRepositorySystem(locator: DefaultServiceLocator): RepositorySystem {
        locator.addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
        locator.addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
        locator.addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        return locator.getService(RepositorySystem::class.java)
    }

    private fun newSession(system: RepositorySystem): RepositorySystemSession {
        val session: DefaultRepositorySystemSession = MavenRepositorySystemUtils.newSession()
        val localRepo = LocalRepository(File("repo/"))
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepo)
        return session
    }

}