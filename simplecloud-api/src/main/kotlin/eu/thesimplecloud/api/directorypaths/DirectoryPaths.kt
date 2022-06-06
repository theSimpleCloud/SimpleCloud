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

package eu.thesimplecloud.api.directorypaths

class DirectoryPaths(
    val groupsPath: String = "groups/",
    val proxyGroupsPath: String = groupsPath + "proxies/",
    val serverGroupsPath: String = groupsPath + "servers/",
    val lobbyGroupsPath: String = groupsPath + "lobbies/",
    val storagePath: String = "storage/",
    val templatesPath: String = "templates/",
    val tempPath: String = "tmp/",
    val staticPath: String = "static/",
    val minecraftJarsPath: String = storagePath + "minecraftJars/",
    val languagesPath: String = storagePath + "languages/",
    val modulesPath: String = "modules/",
    val wrappersPath: String = storagePath + "wrappers/",
    val zippedTemplatesPath: String = storagePath + "zippedTemplates/",
) {
    companion object {
        lateinit var paths: DirectoryPaths
    }
}