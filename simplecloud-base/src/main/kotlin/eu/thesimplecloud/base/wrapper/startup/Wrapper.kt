package eu.thesimplecloud.base.wrapper.startup

import eu.thesimplecloud.base.wrapper.impl.CloudAPIImpl
import eu.thesimplecloud.base.wrapper.logger.LoggerMessageListenerImpl
import eu.thesimplecloud.base.wrapper.network.packets.template.PacketOutGetTemplates
import eu.thesimplecloud.base.wrapper.process.CloudServiceProcessManager
import eu.thesimplecloud.base.wrapper.process.filehandler.ServiceVersionLoader
import eu.thesimplecloud.base.wrapper.process.queue.CloudServiceProcessQueue
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.ServiceConfiguratorManager
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.client.packets.PacketOutCloudClientLogin
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.network.packets.wrapper.PacketIOUpdateWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.base.manager.external.CloudModuleHandler
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.external.module.CloudModuleFileContent
import eu.thesimplecloud.launcher.external.module.CloudModuleLoader
import org.apache.commons.io.FileUtils
import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Wrapper : ICloudApplication {

    companion object {
        lateinit var instance: Wrapper
            private set
    }

    lateinit var thisWrapperName: String
    var processQueue: CloudServiceProcessQueue? = null
    val serviceConfigurationManager = ServiceConfiguratorManager()
    val cloudServiceProcessManager = CloudServiceProcessManager()
    val portManager = PortManager()
    val communicationClient: INettyClient
    val templateClient: INettyClient?
    val serviceVersionLoader = ServiceVersionLoader()
    lateinit var existingModules: List<Pair<CloudModuleFileContent, File>>

    init {
        instance = this
        Launcher.instance.logger.addLoggerMessageListener(LoggerMessageListenerImpl())
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.communicationClient = NettyClient(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        CloudAPIImpl()
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.base.wrapper.network.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.api.network.packets")
        thread(start = true, isDaemon = false) { communicationClient.start() }
        this.communicationClient.getPacketIdsSyncPromise().addResultListener {
            this.communicationClient.sendUnitQuery(PacketOutCloudClientLogin(CloudClientType.WRAPPER))
        }
        if (isStartedInManagerDirectory()) {
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.not-activated", "Detected that a manager is running in this directory. Using templates in this folder.")
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.help-message", "If your'e manager is not running in this directory delete the folder \"storage/wrappers\" and restart the wrapper.")
            this.templateClient = null
        } else {
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.using", "Using an extra client to receive / send templates.")
            this.templateClient = NettyClient(launcherConfig.host, launcherConfig.port + 1, ConnectionHandlerImpl())
            this.communicationClient.getPacketIdsSyncPromise().addResultListener {
                Launcher.instance.scheduler.schedule({ startTemplateClient(this.templateClient) }, 100, TimeUnit.MILLISECONDS)
            }
        }

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            if (this.communicationClient.isOpen()) {
                val wrapperInfo = getThisWrapper()
                //set authenticated to false to prevent service starting
                wrapperInfo.setAuthenticated(false)
                communicationClient.sendUnitQuery(PacketIOUpdateWrapperInfo(wrapperInfo)).syncUninterruptibly()
            }
            this.processQueue?.clearQueue()
            this.cloudServiceProcessManager.stopAllServices()
            while (this.cloudServiceProcessManager.getAllProcesses().isNotEmpty()) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            if (this.templateClient != null) {
                FileUtils.deleteDirectory(File(DirectoryPaths.paths.templatesPath))
            }
            this.communicationClient.shutdown()
            this.templateClient?.shutdown()
        })


    }

    private fun startTemplateClient(templateClient: NettyClient) {
        templateClient.addPacketsByPackage("eu.thesimplecloud.base.wrapper.network.packets.template")
        thread(start = true, isDaemon = false) { templateClient.start() }
        templateClient.getPacketIdsSyncPromise().addResultListener {
            Launcher.instance.consoleSender.sendMessage("wrapper.template.requesting", "Requesting templates...")
            templateClient.sendUnitQuery(PacketOutGetTemplates(), TimeUnit.SECONDS.toMillis((60 * 2) + 30)).addResultListener {
                this.existingModules = CloudModuleHandler().getAllCloudModuleFileContents()
                Launcher.instance.consoleSender.sendMessage("wrapper.template.received", "Templates received.")
            }.addFailureListener {
                Launcher.instance.logger.severe("An error occurred while requesting templates:")
                Launcher.instance.logger.exception(it)
            }
        }
    }

    fun getThisWrapper(): IWrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByName(this.thisWrapperName)
            ?: throw IllegalStateException("Unable to find self wrapper.")

    /**
     * Updates the memory the wrapper currently uses according to the registered service processes.
     */
    fun updateUsedMemory() {
        val usedMemory = this.cloudServiceProcessManager.getAllProcesses().sumBy { it.getCloudService().getMaxMemory() }
        val thisWrapper = this.getThisWrapper()
        thisWrapper as IWritableWrapperInfo
        thisWrapper.setUsedMemory(usedMemory)
        if (this.communicationClient.isOpen())
            this.communicationClient.sendUnitQuery(PacketIOUpdateWrapperInfo(thisWrapper))
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    fun isStartedInManagerDirectory(): Boolean = File("storage/wrappers/").exists()

    fun startProcessQueue() {
        check(processQueue == null) { "Cannot start process queue when it is already running" }
        this.processQueue = CloudServiceProcessQueue(getThisWrapper().getMaxSimultaneouslyStartingServices())
        this.processQueue?.startThread()
    }
}