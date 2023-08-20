/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.runner.dependency.CloudDependency

data class ModuleFileContent(
    val name: String,
    val author: String,
    val mainClass: String,
    val moduleCopyType: ModuleCopyType,
    val repositories: List<String>,
    val dependencies: List<CloudDependency>,
    val depend: List<String>,
    val softDepend: List<String>
) {

    fun isDependencyOf(moduleFileContent: ModuleFileContent): Boolean {
        return moduleFileContent.depend.contains(name)
    }

    fun dependsOn(dependencyFileContent: ModuleFileContent): Boolean {
        return this.depend.contains(dependencyFileContent.name)
    }

    fun dependsOrSoftDependsOn(dependencyFileContent: ModuleFileContent): Boolean {
        return dependsOn(dependencyFileContent) || this.softDepend.contains(dependencyFileContent.name)
    }

}