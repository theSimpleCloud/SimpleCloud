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

package eu.thesimplecloud.module.sign.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.`object`.SynchronizedObjectUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.sign.lib.*
import eu.thesimplecloud.module.sign.manager.command.SignsCommand
import org.apache.commons.io.FileUtils
import java.io.File

class SignsModule : ICloudModule {

    private val signsFile = File("modules/sign/signs.json")
    private val layoutsDir = File("modules/sign/layouts/")
    private val groupToLayoutsFile = File("modules/sign/groupToLayouts.json")


    init {
        INSTANCE = this
    }

    override fun onEnable() {
        registerListener()
        reloadConfig()

        Launcher.instance.commandManager.registerCommand(this, SignsCommand())
    }

    private fun registerListener() {
        CloudAPI.instance.getEventManager().registerListener(this, object : IListener {

            @CloudEventHandler
            fun on(event: SynchronizedObjectUpdatedEvent) {
                val signModuleConfigHolder = event.synchronizedObject
                if (signModuleConfigHolder.obj is SignModuleConfig) {
                    saveConfigToFiles(signModuleConfigHolder.obj as SignModuleConfig)
                }
            }

        })
    }

    fun reloadConfig() {
        val signModuleConfig = loadConfigFromFiles()
        SignModuleConfig.INSTANCE = SynchronizedObjectHolder(signModuleConfig)
        signModuleConfig.update()
    }

    private fun loadConfigFromFiles(): SignModuleConfig {
        val cloudSigns: MutableList<CloudSign> = JsonData.fromJsonFile(signsFile)?.getObject(ArrayList::class.java) as ArrayList<CloudSign>?
                ?: ArrayList()
        val layoutFiles = layoutsDir.listFiles() ?: emptyArray()
        val layouts = layoutFiles.mapNotNull { JsonData.fromJsonFile(it)?.getObject(SignLayout::class.java) }.toMutableList()
        val groupToLayout = JsonData.fromJsonFile(groupToLayoutsFile)?.getObject(GroupToLayout::class.java)
                ?: GroupToLayout()

        if (layouts.isEmpty()) layouts.addAll(getDefaultLayoutList())
        return SignModuleConfig(cloudSigns, groupToLayout, layouts)
    }

    private fun getDefaultLayoutList(): List<SignLayout> {
        return listOf(
                SignLayout("SEARCHING", listOf(
                        SignFrame(arrayOf("§8--------", "Searching for", "server.", "§8--------")),
                        SignFrame(arrayOf("§8--------", "Searching for", "server..", "§8--------")),
                        SignFrame(arrayOf("§8--------", "Searching for", "server...", "§8--------"))
                )),
                SignLayout("STARTING", listOf(
                        SignFrame(arrayOf("§6--------", "Server is", "starting.", "§6--------")),
                        SignFrame(arrayOf("§6--------", "Server is", "starting..", "§6--------")),
                        SignFrame(arrayOf("§6--------", "Server is", "starting...", "§6--------"))
                )),
                SignLayout("MAINTENANCE", listOf(
                        SignFrame(arrayOf("§4--------", "%GROUP%", "§8Maintenance", "§4--------"))
                )),
                SignLayout("default", listOf(
                        SignFrame(arrayOf("%SERVICE%", "§a%STATE%", "%MOTD%", "%ONLINE_PLAYERS%/%MAX_PLAYERS%"))
                ))
        )
    }

    fun saveConfigToFiles(signModuleConfig: SignModuleConfig) {
        FileUtils.deleteDirectory(this.layoutsDir)
        this.layoutsDir.mkdirs()
        signModuleConfig.signLayouts.forEach {
            JsonData.fromObject(it).saveAsFile(File(this.layoutsDir, it.name + ".json"))
        }
        JsonData.fromObject(signModuleConfig.cloudSigns).saveAsFile(this.signsFile)
        JsonData.fromObject(signModuleConfig.groupToLayout).saveAsFile(this.groupToLayoutsFile)
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var INSTANCE: SignsModule
            private set
    }
}