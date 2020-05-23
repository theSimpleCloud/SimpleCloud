package eu.thesimplecloud.base.wrapper.startup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWritableWrapperInfo
import eu.thesimplecloud.base.wrapper.impl.CloudAPIImpl
import eu.thesimplecloud.base.wrapper.logger.LoggerMessageListenerImpl
import eu.thesimplecloud.base.wrapper.network.packets.template.PacketOutGetTemplates
import eu.thesimplecloud.base.wrapper.process.CloudServiceProcessManager
import eu.thesimplecloud.base.wrapper.process.filehandler.ServiceVersionLoader
import eu.thesimplecloud.base.wrapper.process.queue.CloudServiceProcessQueue
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.ServiceConfiguratorManager
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.config.LauncherConfig
import eu.thesimplecloud.launcher.exception.module.ModuleHandler
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.external.module.LoadedModuleFileContent
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader
import eu.thesimplecloud.launcher.startup.Launcher
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class Wrapper : ICloudApplication {

    companion object {
        @JvmStatic
        lateinit var instance: Wrapper
            private set
    }

    @Volatile
    var thisWrapperName: String? = null
    var processQueue: CloudServiceProcessQueue? = null
    val serviceConfigurationManager = ServiceConfiguratorManager()
    val cloudServiceProcessManager = CloudServiceProcessManager()
    val portManager = PortManager()
    val communicationClient: INettyClient
    var templateClient: INettyClient? = null
        private set
    val serviceVersionLoader = ServiceVersionLoader()
    var existingModules: List<LoadedModuleFileContent> = ArrayList()
        private set
    val appClassLoader: ModuleClassLoader

    init {
        instance = this
        this.appClassLoader = this::class.java.classLoader as ModuleClassLoader
        Launcher.instance.logger.addLoggerMessageListener(LoggerMessageListenerImpl())
        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.communicationClient = NettyClient(launcherConfig.host, launcherConfig.port, ConnectionHandlerImpl())
        this.communicationClient.setPacketSearchClassLoader(Launcher.instance.getNewClassLoaderWithLauncherAndBase())
        this.communicationClient.setClassLoaderToSearchObjectPacketClasses(appClassLoader)
        this.communicationClient.setPacketClassConverter { Class.forName(it.name, true, appClassLoader) as Class<out IPacket> }
        CloudAPIImpl()
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.client.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.base.wrapper.network.packets")
        this.communicationClient.addPacketsByPackage("eu.thesimplecloud.api.network.packets")
        thread(start = true, isDaemon = true) {
            resetWrapperAndStartReconnectLoop(launcherConfig)
        }
        if (isStartedInManagerDirectory()) {
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.not-activated", "Detected that a manager is running in this directory. Using templates in this folder.")
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.help-message", "If your'e manager is not running in this directory delete the folder \"storage/wrappers\" and restart the wrapper.")
            this.templateClient = null
        } else {
            Launcher.instance.consoleSender.sendMessage("wrapper.startup.template-client.using", "Using an extra client to receive / send templates.")
        }

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            if (this.communicationClient.isOpen()) {
                val wrapperInfo = getThisWrapper()
                //set authenticated to false to prevent service starting
                wrapperInfo.setAuthenticated(false)
                CloudAPI.instance.getWrapperManager().update(wrapperInfo)
            }
            this.processQueue?.clearQueue()
            stopAllRunningServicesAndWaitFor()
            if (this.templateClient != null) {
                FileUtils.deleteDirectory(File(DirectoryPaths.paths.modulesPath))
            }
            this.communicationClient.shutdown()
            this.templateClient?.shutdown()
        })
    }

    private fun stopAllRunningServicesAndWaitFor() {
        this.cloudServiceProcessManager.stopAllServices()
        while (this.cloudServiceProcessManager.getAllProcesses().isNotEmpty()) {
            Thread.sleep(100)
        }
    }

    /**
     * Starts this client. This method will return when the client is fully connected to the manager.
     */
    fun resetWrapperAndStartReconnectLoop(launcherConfig: LauncherConfig) {
        //reset wrapper
        this.stopAllRunningServicesAndWaitFor()
        this.communicationClient.shutdown().awaitUninterruptibly()
        this.templateClient?.shutdown()?.awaitUninterruptibly()
        this.existingModules = ArrayList()
        this.thisWrapperName = null
        this.processQueue = null

        while (!this.communicationClient.start().awaitUninterruptibly().isSuccess) {
            Launcher.instance.consoleSender.sendMessage("wrapper.connection-failed", "Failed to connect to manager. Retrying in 5 seconds.")
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
            }
        }

        if (!isStartedInManagerDirectory()) {
            val templateClient = NettyClient(launcherConfig.host, launcherConfig.port + 1, ConnectionHandlerImpl())
            this.templateClient = templateClient
            templateClient.setPacketSearchClassLoader(Launcher.instance.getNewClassLoaderWithLauncherAndBase())
            templateClient.setClassLoaderToSearchObjectPacketClasses(appClassLoader)
            Launcher.instance.scheduler.schedule({ startTemplateClient(templateClient) }, 100, TimeUnit.MILLISECONDS)
        } else {
            reloadExistingModules()
        }

    }

    private fun startTemplateClient(templateClient: NettyClient) {
        templateClient.addPacketsByPackage("eu.thesimplecloud.base.wrapper.network.packets.template")
        thread(start = true, isDaemon = false) {
            templateClient.start().then {
                Launcher.instance.consoleSender.sendMessage("wrapper.template.requesting", "Requesting templates...")
                templateClient.sendUnitQuery(PacketOutGetTemplates(), TimeUnit.SECONDS.toMillis((60 * 2) + 30)).addResultListener {
                    reloadExistingModules()
                    val thisWrapper = getThisWrapper() as IWritableWrapperInfo
                    thisWrapper.setTemplatesReceived(true)
                    CloudAPI.instance.getWrapperManager().update(thisWrapper)
                    Launcher.instance.consoleSender.sendMessage("wrapper.template.received", "Templates received.")
                }.addFailureListener {
                    Launcher.instance.logger.severe("An error occurred while requesting templates:")
                    Launcher.instance.logger.exception(it)
                }
            }
        }
    }

    @Synchronized
    fun reloadExistingModules() {
        this.existingModules = ModuleHandler().getAllCloudModuleFileContents()
    }

    fun isWrapperNameSet(): Boolean = thisWrapperName != null

    fun getThisWrapper(): IWrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByName(this.thisWrapperName!!)
            ?: throw IllegalStateException("Unable to find self wrapper.")

    /**
     * Updates the memory the wrapper currently uses according to the registered service processes.
     */
    fun updateWrapperData() {
        val usedMemory = this.cloudServiceProcessManager.getAllProcesses().sumBy { it.getCloudService().getMaxMemory() }
        val thisWrapper = this.getThisWrapper()
        thisWrapper as IWritableWrapperInfo
        thisWrapper.setUsedMemory(usedMemory)
        thisWrapper.setCurrentlyStartingServices(this.processQueue?.getStartingOrQueuedServiceAmount() ?: 0)
        if (this.communicationClient.isOpen())
            CloudAPI.instance.getWrapperManager().update(thisWrapper)
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    fun isStartedInManagerDirectory(): Boolean = File("storage/wrappers/").exists()

    fun startProcessQueue() {
        check(processQueue == null) { "Cannot start process queue when it is already running" }
        this.processQueue = CloudServiceProcessQueue()
        this.processQueue?.startThread()
    }
}