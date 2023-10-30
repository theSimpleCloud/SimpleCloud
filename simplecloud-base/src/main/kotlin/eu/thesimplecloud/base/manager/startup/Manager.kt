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

package eu.thesimplecloud.base.manager.startup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.base.core.jvm.JvmArgumentsConfig
import eu.thesimplecloud.base.manager.config.JvmArgumentsConfigLoader
import eu.thesimplecloud.base.manager.config.encryption.KeyConfigLoader
import eu.thesimplecloud.base.manager.config.mongo.DatabaseConfigLoader
import eu.thesimplecloud.base.manager.config.template.TemplatesConfigLoader
import eu.thesimplecloud.base.manager.config.updater.ModuleUpdaterConfigLoader
import eu.thesimplecloud.base.manager.database.*
import eu.thesimplecloud.base.manager.database.aes.AdvancedEncryption
import eu.thesimplecloud.base.manager.filehandler.CloudServiceGroupFileHandler
import eu.thesimplecloud.base.manager.filehandler.WrapperFileHandler
import eu.thesimplecloud.base.manager.impl.CloudAPIImpl
import eu.thesimplecloud.base.manager.ingamecommands.IngameCommandUpdater
import eu.thesimplecloud.base.manager.listener.CloudListener
import eu.thesimplecloud.base.manager.listener.ModuleEventListener
import eu.thesimplecloud.base.manager.packet.IPacketRegistry
import eu.thesimplecloud.base.manager.packet.PacketRegistry
import eu.thesimplecloud.base.manager.player.PlayerUnregisterScheduler
import eu.thesimplecloud.base.manager.service.ServiceHandler
import eu.thesimplecloud.base.manager.setup.CreateDefaultLobbyGroup
import eu.thesimplecloud.base.manager.setup.CreateDefaultProxyGroup
import eu.thesimplecloud.base.manager.setup.database.DatabaseConnectionSetup
import eu.thesimplecloud.base.manager.startup.server.CommunicationConnectionHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.ManagerAccessHandler
import eu.thesimplecloud.base.manager.startup.server.ServerHandlerImpl
import eu.thesimplecloud.base.manager.startup.server.TemplateConnectionHandlerImpl
import eu.thesimplecloud.base.manager.update.converter.VersionConversionManager
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.launcher.application.ApplicationClassLoader
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader
import eu.thesimplecloud.launcher.external.module.handler.IModuleHandler
import eu.thesimplecloud.launcher.external.module.handler.ModuleHandler
import eu.thesimplecloud.launcher.language.LanguageFileLoader
import eu.thesimplecloud.launcher.startup.Launcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread

class Manager : ICloudApplication {

    val ingameCommandUpdater: IngameCommandUpdater
    val cloudServiceGroupFileHandler = CloudServiceGroupFileHandler()
    val wrapperFileHandler = WrapperFileHandler()
    val templatesConfigLoader = TemplatesConfigLoader()
    val serviceHandler: ServiceHandler

    val offlineCloudPlayerHandler: IOfflineCloudPlayerHandler

    val communicationServer: INettyServer<ICommandExecutable>
    val templateServer: INettyServer<ICommandExecutable>
    val packetRegistry: IPacketRegistry = PacketRegistry()
    val playerUnregisterScheduler = PlayerUnregisterScheduler()
    val cloudModuleHandler: IModuleHandler
    val appClassLoader: ApplicationClassLoader
    val encryption: AdvancedEncryption

    private val profileFile = ProfileFile()

    lateinit var jvmArgumentsConfig: JvmArgumentsConfig

    companion object {
        @JvmStatic
        lateinit var instance: Manager
            private set
    }

    init {
        Logger.getLogger("org.mongodb.driver").level = Level.SEVERE
        instance = this
        VersionConversionManager().convertIfNecessary()
        CloudAPIImpl()
        LanguageFileLoader().loadFile(Launcher.instance.launcherConfig)
        this.serviceHandler = ServiceHandler()
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener())
        CloudAPI.instance.getEventManager().registerListener(this, ModuleEventListener())
        this.appClassLoader = this::class.java.classLoader as ApplicationClassLoader

        this.cloudModuleHandler = ModuleHandler(
            appClassLoader,
            Launcher.instance.launcherConfig.language,
            ModuleUpdaterConfigLoader().loadConfig().modules,
            true
        ) { Launcher.instance.logger.exception(it) }
        this.appClassLoader.moduleHandler = this.cloudModuleHandler

        this.cloudModuleHandler.setCreateModuleClassLoader { urls, name ->
            ModuleClassLoader(
                urls,
                this.appClassLoader,
                name,
                this.cloudModuleHandler
            )
        }
        Property.propertyClassFindFunction = { this.cloudModuleHandler.findModuleOrSystemClass(it) }
        this.ingameCommandUpdater = IngameCommandUpdater()
        if (!DatabaseConfigLoader().doesConfigFileExist()) {
            Launcher.instance.setupManager.queueSetup(DatabaseConnectionSetup())
            Launcher.instance.setupManager.waitForAllSetups()
        }
        val mongoConnectionInformation = DatabaseConfigLoader().loadConfig()
        this.encryption = AdvancedEncryption(KeyConfigLoader().loadConfig())

        val launcherConfig = Launcher.instance.launcherConfig
        val baseAndLauncherLoader = Launcher.instance.getNewClassLoaderWithLauncherAndBase()
        this.communicationServer = NettyServer<ICommandExecutable>(
            launcherConfig.host,
            launcherConfig.port,
            CommunicationConnectionHandlerImpl(),
            ServerHandlerImpl()
        )
        this.communicationServer.setAccessHandler(ManagerAccessHandler())
        this.communicationServer.setPacketSearchClassLoader(baseAndLauncherLoader)
        this.communicationServer.setClassLoaderToSearchObjectPacketClasses(appClassLoader)
        this.communicationServer.setPacketClassConverter { moveToApplicationClassLoader(it) }
        this.templateServer = NettyServer<ICommandExecutable>(
            launcherConfig.host,
            launcherConfig.port + 1,
            TemplateConnectionHandlerImpl(),
            ServerHandlerImpl()
        )
        this.templateServer.setAccessHandler(ManagerAccessHandler())
        this.templateServer.setPacketSearchClassLoader(baseAndLauncherLoader)
        this.templateServer.setClassLoaderToSearchObjectPacketClasses(appClassLoader)
        this.templateServer.setPacketClassConverter { moveToApplicationClassLoader(it) }
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.api.network.packets")
        this.communicationServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets")
        this.templateServer.addPacketsByPackage("eu.thesimplecloud.base.manager.network.packets.template")
        thread(start = true, isDaemon = false) { communicationServer.start() }
        createDirectories()
        Logger.getLogger("org.mongodb.driver").level = Level.SEVERE
        Launcher.instance.logger.console("Waiting for the database...")

        this.offlineCloudPlayerHandler = when (mongoConnectionInformation.databaseType) {
            DatabaseType.MONGODB -> MongoOfflineCloudPlayerHandler(mongoConnectionInformation)
            DatabaseType.MYSQL -> SQLOfflineCloudPlayerHandler(mongoConnectionInformation)
            DatabaseType.SQLITE -> SQLiteOfflineCloudPlayerHandler(mongoConnectionInformation)
        }
        Launcher.instance.logger.console("Connected to the database")

        this.templateServer.getDirectorySyncManager().setTmpZipDirectory(File(DirectoryPaths.paths.zippedTemplatesPath))
        this.templateServer.getDirectorySyncManager()
            .createDirectorySync(File(DirectoryPaths.paths.templatesPath), DirectoryPaths.paths.templatesPath)
        this.templateServer.getDirectorySyncManager()
            .createDirectorySync(File(DirectoryPaths.paths.modulesPath), DirectoryPaths.paths.modulesPath)
        this.serviceHandler.startThread()
        thread(start = true, isDaemon = false) { templateServer.start() }
        VersionConversionManager().writeLastStartedVersionIfFileDoesNotExist()
        //this.playerUnregisterScheduler.startScheduler()
    }

    private fun moveToApplicationClassLoader(clazz: Class<out IPacket>): Class<out IPacket> {
        if (appClassLoader.isThisClassLoader(clazz)) return clazz
        val loadedClass = appClassLoader.loadClass(clazz.name)
        appClassLoader.setCachedClass(loadedClass)
        return loadedClass as Class<out IPacket>
    }

    override fun onEnable() {
        GlobalScope.launch {
            Launcher.instance.commandManager.registerAllCommands(
                instance,
                appClassLoader,
                "eu.thesimplecloud.base.manager.commands"
            )
        }
        Launcher.instance.setupManager.waitForAllSetups()
        this.wrapperFileHandler.loadAll().forEach { CloudAPI.instance.getWrapperManager().update(it) }
        this.cloudServiceGroupFileHandler.loadAll()
            .forEach { CloudAPI.instance.getCloudServiceGroupManager().update(it) }
        val templates = this.templatesConfigLoader.loadConfig().templates
        templates.forEach { CloudAPI.instance.getTemplateManager().update(it) }
        this.jvmArgumentsConfig = JvmArgumentsConfigLoader().loadConfig()

        if (CloudAPI.instance.getWrapperManager().getAllCachedObjects().isNotEmpty()) {
            Launcher.instance.consoleSender.sendProperty("manager.startup.loaded.wrappers")
            CloudAPI.instance.getWrapperManager().getAllCachedObjects()
                .forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }

        if (CloudAPI.instance.getTemplateManager().getAllCachedObjects().isNotEmpty()) {
            Launcher.instance.consoleSender.sendProperty("manager.startup.loaded.templates")
            CloudAPI.instance.getTemplateManager().getAllCachedObjects()
                .forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }

        if (CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects().isNotEmpty()) {
            Launcher.instance.consoleSender.sendProperty("manager.startup.loaded.groups")
            CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects()
                .forEach { Launcher.instance.consoleSender.sendMessage("- ${it.getName()}") }
        }
        thread(start = true, isDaemon = false, appClassLoader) {
            this.cloudModuleHandler.loadAllUnloadedModules()
        }

        if (CloudAPI.instance.getCloudServiceGroupManager().getAllCachedObjects().isEmpty()) {
            Launcher.instance.setupManager.queueSetup(CreateDefaultProxyGroup())
            Launcher.instance.setupManager.waitForAllSetups()
            Launcher.instance.setupManager.queueSetup(CreateDefaultLobbyGroup())
        }

        try {
            this.profileFile.create()
        } catch (exception: Exception) {
            Launcher.instance.logger.warning("An error occurred while creating the profile file: ${exception.message}")
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
        this.serviceHandler.stopThread()
        this.cloudModuleHandler.unloadAllModules()
        this.profileFile.create()
    }

}