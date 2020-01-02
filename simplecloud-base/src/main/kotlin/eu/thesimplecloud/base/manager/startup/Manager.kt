package eu.thesimplecloud.base.manager.startup

import com.mongodb.MongoClient
import eu.thesimplecloud.base.MongoBuilder
import eu.thesimplecloud.base.MongoController
import eu.thesimplecloud.base.manager.config.MongoConfigLoader
import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.config.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudLibImpl
import eu.thesimplecloud.base.manager.listener.CloudListener
import eu.thesimplecloud.base.manager.mongo.MongoConnectionInformation
import eu.thesimplecloud.base.manager.mongo.MongoServerInformation
import eu.thesimplecloud.base.manager.player.IOfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.player.OfflineCloudPlayerHandler
import eu.thesimplecloud.base.manager.service.ServiceHandler
import eu.thesimplecloud.base.manager.setup.mongo.MongoDBUseEmbedSetup
import eu.thesimplecloud.base.manager.startup.server.CommunicationConnectionHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.ServerHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.TemplateConnectionHandlerImpl
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.screen.ICommandExecutable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.KMongo
import java.io.File
import kotlin.concurrent.thread

class Manager : ICloudApplication {

    val cloudServiceGroupFileHandler = CloudServiceGroupFileHandler()
    val wrapperFileHandler = WrapperFileHandler()
    val templatesConfigLoader = TemplatesConfigLoader()
    val serviceHandler: ServiceHandler = ServiceHandler()
    // only set when embed mongodb is used
    var mongoController: MongoController? = null
        private set
    val mongoClient: MongoClient

    val offlineCloudPlayerLoader: IOfflineCloudPlayerHandler

    val communicationServer: INettyServer<ICommandExecutable>
    val templateServer: INettyServer<ICommandExecutable>

    companion object {
        lateinit var instance: Manager
    }

    init {

        instance = this
        CloudLibImpl()
        CloudLib.instance.getEventManager().registerListener(this, CloudListener())
        if (!MongoConfigLoader().doesConfigFileExist()) {
            Launcher.instance.setupManager.queueSetup(MongoDBUseEmbedSetup())
            Launcher.instance.setupManager.waitFroAllSetups()
        }
        val mongoConfig = MongoConfigLoader().loadConfig()
        if (mongoConfig.embedMongo)
            mongoController = startMongoDBServer(mongoConfig.mongoServerInformation)

        val launcherConfig = Launcher.instance.launcherConfigLoader.loadConfig()
        this.communicationServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port, CommunicationConnectionHandlerImpl(), ServerHandlerImpl())
        this.templateServer = NettyServer<ICommandExecutable>(launcherConfig.host, launcherConfig.port + 1, TemplateConnectionHandlerImpl(), ServerHandlerImpl())
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.lib.network.packets")
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets")
        this.templateServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets.template")
        createDirectories()
        Launcher.instance.logger.console("Waiting for MongoDB...")
        this.mongoController?.startedPromise?.awaitUninterruptibly()
        mongoClient = connectToMongo(mongoConfig.mongoServerInformation)
        Launcher.instance.logger.console("Connected to MongoDB")

        this.offlineCloudPlayerLoader = OfflineCloudPlayerHandler(mongoConfig.mongoServerInformation)

        thread(start = true, isDaemon = false) { templateServer.start() }
        thread(start = true, isDaemon = false) { communicationServer.start() }
        this.templateServer.getDirectorySyncManager().createDirectorySync(File(DirectoryPaths.paths.templatesPath), DirectoryPaths.paths.templatesPath)
        this.templateServer.getDirectorySyncManager().createDirectorySync(File(DirectoryPaths.paths.modulesPath), DirectoryPaths.paths.modulesPath)
        this.serviceHandler.startThread()
    }

    private fun startMongoDBServer(mongoServerInformation: MongoServerInformation): MongoController {
        val mongoController = MongoController(MongoBuilder()
                .setHost(mongoServerInformation.host)
                .setPort(mongoServerInformation.port)
                .setAdminUserName(mongoServerInformation.adminUserName)
                .setAdminPassword(mongoServerInformation.adminPassword)
                .setDatabase(mongoServerInformation.databaseName)
                .setDirectory(".mongo")
                .setUserName(mongoServerInformation.userName)
                .setUserPassword(mongoServerInformation.password))
        mongoController.start()
        return mongoController
    }

    private fun connectToMongo(mongoConnectionInformation: MongoConnectionInformation): MongoClient {
        return KMongo.createClient(mongoConnectionInformation.getConnectionString())
    }

    override fun onEnable() {
        GlobalScope.launch { Launcher.instance.commandManager.registerAllCommands(instance, "eu.thesimplecloud.base.manager.commands") }
        Launcher.instance.setupManager.waitFroAllSetups()
        this.wrapperFileHandler.loadAll().forEach { CloudLib.instance.getWrapperManager().updateWrapper(it) }
        this.cloudServiceGroupFileHandler.loadAll().forEach { CloudLib.instance.getCloudServiceGroupManager().updateGroup(it) }
        this.templatesConfigLoader.loadConfig().templates.forEach { CloudLib.instance.getTemplateManager().updateTemplate(it) }

        if (CloudLib.instance.getWrapperManager().getAllWrappers().isNotEmpty()) {
            Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.wrappers", "Loaded following wrappers:")
            CloudLib.instance.getWrapperManager().getAllWrappers().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }

        if (CloudLib.instance.getTemplateManager().getAllTemplates().isNotEmpty()) {
            Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.templates", "Loaded following templates:")
            CloudLib.instance.getTemplateManager().getAllTemplates().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }

        if (CloudLib.instance.getCloudServiceGroupManager().getAllGroups().isNotEmpty()) {
            Launcher.instance.consoleSender.sendMessage("manager.startup.loaded.groups", "Loaded following groups:")
            CloudLib.instance.getCloudServiceGroupManager().getAllGroups().forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }
    }


    private fun createDirectories() {
        for (file in listOf(
                File(DirectoryPaths.paths.storagePath),
                File(DirectoryPaths.paths.wrappersPath),
                File(DirectoryPaths.paths.minecraftJarsPath),
                File(DirectoryPaths.paths.serverGroupsPath),
                File(DirectoryPaths.paths.lobbyGroupsPath),
                File(DirectoryPaths.paths.proxyGroupsPath),
                File(DirectoryPaths.paths.languagesPath),
                File(DirectoryPaths.paths.modulesPath),
                File(DirectoryPaths.paths.templatesPath),
                File(DirectoryPaths.paths.templatesPath + "EVERY"),
                File(DirectoryPaths.paths.templatesPath + "EVERY_SERVER"),
                File(DirectoryPaths.paths.templatesPath + "EVERY_PROXY")
        )) {
            file.mkdirs()
        }
    }

    override fun onDisable() {
        this.mongoClient.close()
        this.mongoController?.stop()?.awaitUninterruptibly()
    }


}