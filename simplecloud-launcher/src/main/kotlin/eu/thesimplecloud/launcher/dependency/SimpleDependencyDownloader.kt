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

import java.io.IOException


/**
 * Created by IntelliJ IDEA.
 * Date: 31.08.2020
 * Time: 15:39
 * @author Frederick Baier
 */
class SimpleDependencyDownloader(private val repositories: List<String>) {


    fun downloadOnlyJar(dependency: LauncherCloudDependency) {
        if (dependency.getDownloadedFile().exists()) return
        this.repositories.forEach { repoUrl ->
            if (dependency.existInRepo(repoUrl)) {
                dependency.download(repoUrl)
            }
        }
    }

    fun downloadFiles(dependency: LauncherCloudDependency) {
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
    private fun downloadAnyways(dependency: LauncherCloudDependency, repoUrl: String) {
        dependency.download(repoUrl)
        dependency.resolveDependenciesAndSaveToInfoFile(repoUrl)
    }


}