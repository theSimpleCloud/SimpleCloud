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

package eu.thesimplecloud.loader.dependency

import eu.thesimplecloud.runner.dependency.AdvancedCloudDependency
import java.io.File


class LauncherDependencyLoader {

    fun loadLauncherDependencies(): Set<File> {
        val dependencyLoader = DependencyLoader.INSTANCE
        return dependencyLoader.loadDependencies(
            listOf(
                "https://repo.maven.apache.org/maven2/",
                "https://repo.thesimplecloud.eu/artifactory/gradle-release-local/"
            ),
            listOf(
                AdvancedCloudDependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "4.1.17"),
                AdvancedCloudDependency("org.apache.commons", "commons-lang3", "3.12.0"),
                AdvancedCloudDependency("org.slf4j", "slf4j-nop", "1.7.32"),
                AdvancedCloudDependency("org.fusesource.jansi", "jansi", "2.4.0"),
                AdvancedCloudDependency("org.jline", "jline", "3.21.0"),
                AdvancedCloudDependency("org.litote.kmongo", "kmongo", "4.4.0"),
                AdvancedCloudDependency("commons-io", "commons-io", "2.11.0"),
                AdvancedCloudDependency("org.slf4j", "slf4j-simple", "1.7.32"),
                AdvancedCloudDependency("com.google.guava", "guava", "31.0.1-jre"),
                AdvancedCloudDependency("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.2"),
                AdvancedCloudDependency("com.google.code.gson", "gson", "2.8.9"),
                AdvancedCloudDependency("io.netty", "netty-all", "4.1.71.Final"),
                AdvancedCloudDependency("org.reflections", "reflections", "0.9.12"),
                AdvancedCloudDependency("org.mariadb.jdbc", "mariadb-java-client", "2.7.4"),
                AdvancedCloudDependency("com.github.ajalt", "clikt", "2.8.0")
            )
        )
    }

}