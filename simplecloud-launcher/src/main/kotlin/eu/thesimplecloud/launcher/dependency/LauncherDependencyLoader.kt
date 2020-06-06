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

import eu.thesimplecloud.api.depedency.Dependency

class LauncherDependencyLoader {

    fun loadLauncherDependencies() {
        val dependencyLoader = DependencyLoader.INSTANCE
        dependencyLoader.addRepositories("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-dev-local/")
        dependencyLoader.addDependencies(
                Dependency("org.jline", "jline", "3.14.0"),
                Dependency("org.litote.kmongo", "kmongo", "3.11.2"),
                Dependency("commons-io", "commons-io", "2.6"),
                Dependency("org.slf4j", "slf4j-simple", "1.7.10"),
                Dependency("com.google.guava", "guava", "21.0"),
                Dependency("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.5"),
                Dependency("com.google.code.gson", "gson", "2.8.6"),
                Dependency("io.netty", "netty-all", "4.1.49.Final"),
                Dependency("org.reflections", "reflections", "0.9.12"),
                Dependency("com.github.ajalt", "clikt", "2.2.0"))
        dependencyLoader.installDependencies()
    }

}