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

package eu.thesimplecloud.module.sign.manager

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.lib.group.GroupLayoutsContainer
import eu.thesimplecloud.module.sign.lib.layout.LayoutType
import eu.thesimplecloud.module.sign.lib.layout.SignFrame
import eu.thesimplecloud.module.sign.lib.layout.SignLayout
import eu.thesimplecloud.module.sign.lib.layout.SignLayoutContainer
import eu.thesimplecloud.module.sign.lib.sign.CloudSignContainer
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 17:11
 * @author Frederick Baier
 */
object SignModuleConfigPersistence {

    private val layoutsDir = File("modules/sign/layouts/")

    private val signsFile = File("modules/sign/signs.json")
    private val groupLayoutsFile = File("modules/sign/groupLayouts.json")

    fun save(signModuleConfig: SignModuleConfig) {
        JsonLib.fromObject(signModuleConfig.groupsLayoutContainer).saveAsFile(groupLayoutsFile)
        JsonLib.fromObject(signModuleConfig.signContainer).saveAsFile(signsFile)

        val allLayouts = signModuleConfig.signLayoutContainer.getAllLayouts()
        allLayouts.forEach {
            val dir = getLayoutsDirectoryByLayoutType(it.layoutType)
            JsonLib.fromObject(it).saveAsFile(File(dir, it.getName() + ".json"))
        }
    }

    private fun getLayoutsDirectoryByLayoutType(layoutType: LayoutType): File {
        return File(layoutsDir, layoutType.name.toLowerCase() + "/")
    }

    fun load(): SignModuleConfig {
        if (!groupLayoutsFile.exists()) return createDefaultConfig()
        if (!signsFile.exists()) return createDefaultConfig()

        val groupsLayoutContainer =
            JsonLib.fromJsonFile(groupLayoutsFile)!!.getObject(GroupLayoutsContainer::class.java)
        val cloudSignContainer = JsonLib.fromJsonFile(signsFile)!!.getObject(CloudSignContainer::class.java)

        val allLayoutDirectories = LayoutType.values().map { getLayoutsDirectoryByLayoutType(it) }
        val allFiles = allLayoutDirectories.map { it.listFiles().toList() }.flatten()

        val layouts = allFiles.map { JsonLib.fromJsonFile(it)!!.getObject(SignLayout::class.java) }
        return SignModuleConfig(SignLayoutContainer(layouts.toMutableList()), cloudSignContainer, groupsLayoutContainer)
    }

    private fun createDefaultConfig(): SignModuleConfig {
        val groupsLayoutContainer = GroupLayoutsContainer()
        val cloudSignContainer = CloudSignContainer()
        val layoutsContainer = SignLayoutContainer(getDefaultLayoutList().toMutableList())
        return SignModuleConfig(layoutsContainer, cloudSignContainer, groupsLayoutContainer)
    }

    private fun getDefaultLayoutList(): List<SignLayout> {
        return listOf(
            SignLayout(
                "default", LayoutType.SEARCHING, listOf(
                    SignFrame(arrayOf("§8--------", "Searching for", "server.", "§8--------")),
                    SignFrame(arrayOf("§8--------", "Searching for", "server..", "§8--------")),
                    SignFrame(arrayOf("§8--------", "Searching for", "server...", "§8--------"))
                )
            ),
            SignLayout(
                "default", LayoutType.STARTING, listOf(
                    SignFrame(arrayOf("§6--------", "Server is", "starting.", "§6--------")),
                    SignFrame(arrayOf("§6--------", "Server is", "starting..", "§6--------")),
                    SignFrame(arrayOf("§6--------", "Server is", "starting...", "§6--------"))
                )
            ),
            SignLayout(
                "default", LayoutType.MAINTENANCE, listOf(
                    SignFrame(arrayOf("§4--------", "%GROUP%", "§8Maintenance", "§4--------"))
                )
            ),
            SignLayout(
                "default", LayoutType.ONLINE, listOf(
                    SignFrame(arrayOf("%DISPLAYNAME%", "§a%STATE%", "%MOTD%", "%ONLINE_PLAYERS%/%MAX_PLAYERS%"))
                )
            ),
            SignLayout(
                "default", LayoutType.FULL, listOf(
                    SignFrame(arrayOf("%DISPLAYNAME%", "§6%STATE%", "%MOTD%", "%ONLINE_PLAYERS%/%MAX_PLAYERS%"))
                )
            )
        )
    }

}