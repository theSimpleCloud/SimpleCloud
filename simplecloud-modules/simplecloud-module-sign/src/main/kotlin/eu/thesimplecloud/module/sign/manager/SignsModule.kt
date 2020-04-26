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
            JsonData.fromObjectWithGsonExclude(it).saveAsFile(File(this.layoutsDir, it.name + ".json"))
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