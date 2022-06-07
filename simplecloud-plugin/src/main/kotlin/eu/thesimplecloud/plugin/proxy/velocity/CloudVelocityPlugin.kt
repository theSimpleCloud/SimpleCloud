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

package eu.thesimplecloud.plugin.proxy.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.plugin.impl.player.CloudPlayerManagerVelocity
import eu.thesimplecloud.plugin.listener.CloudListener
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin
import eu.thesimplecloud.plugin.proxy.ProxyEventHandler
import eu.thesimplecloud.plugin.proxy.velocity.commands.VelocityCommand
import eu.thesimplecloud.plugin.proxy.velocity.listener.CloudPlayerDisconnectListener
import eu.thesimplecloud.plugin.proxy.velocity.listener.VelocityListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:11
 */

@Plugin(id = "simplecloud_plugin")
class CloudVelocityPlugin @Inject constructor(val proxyServer: ProxyServer) : ICloudProxyPlugin {

    companion object {
        @JvmStatic
        lateinit var instance: CloudVelocityPlugin
    }

    @Volatile
    lateinit var synchronizedIngameCommandsProperty: IProperty<Array<String>>
        private set
    val lobbyConnector = LobbyConnector()

    init {
        instance = this

        CloudPlugin(this)
        val synchronizedObjectPromise =
            CloudAPI.instance.getGlobalPropertyHolder().requestProperty<Array<String>>("simplecloud-ingamecommands")
        synchronizedObjectPromise.addResultListener { property ->
            this.synchronizedIngameCommandsProperty = property

            this.synchronizedIngameCommandsProperty.getValue().forEach {
                proxyServer.commandManager.unregister(it)
            }

            this.synchronizedIngameCommandsProperty.getValue().forEach {
                val commandManager = proxyServer.commandManager
                commandManager.register(commandManager.metaBuilder(it).build(), VelocityCommand(it))
            }
        }.throwFailure()
    }

    @Subscribe
    fun handleInit(event: ProxyInitializeEvent) {
        CloudPlugin.instance.onEnable()
        CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().filter { it.isActive() }
            .forEach { addServiceToProxy(it) }
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudListener())
        proxyServer.eventManager.register(this, VelocityListener(this))

        synchronizeOnlineCountTask()
        runOfflinePlayerChecker()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, CloudPlayerDisconnectListener(this.proxyServer))
    }


    @Subscribe
    fun handleShutdown(event: ProxyShutdownEvent) {
        CloudPlugin.instance.onDisable()
    }

    override fun addServiceToProxy(cloudService: ICloudService) {
        if (proxyServer.getServer(cloudService.getName()).isPresent) {
            throw IllegalArgumentException("Service is already registered!")
        }
        if (cloudService.getServiceType().isProxy())
            return
        val cloudServiceGroup = cloudService.getServiceGroup()
        if ((cloudServiceGroup as ICloudServerGroup).getHiddenAtProxyGroups()
                .contains(CloudPlugin.instance.getGroupName())
        )
            return
        println("Registered service ${cloudService.getName()}")
        val socketAddress = InetSocketAddress(cloudService.getHost(), cloudService.getPort())
        registerService(cloudService.getName(), socketAddress)
    }

    private fun registerService(name: String, socketAddress: InetSocketAddress) {
        val info = ServerInfo(name, socketAddress)
        proxyServer.registerServer(info)
    }

    override fun removeServiceFromProxy(cloudService: ICloudService) {
        val registeredServer = proxyServer.getServer(cloudService.getName()).orElse(null) ?: return

        proxyServer.unregisterServer(registeredServer.serverInfo)
    }

    private fun runOfflinePlayerChecker() {
        proxyServer.scheduler.buildTask(this) {
            val playersOnThisService =
                CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects()
            val playersOffline = playersOnThisService.filter { proxyServer.getPlayer(it.getUniqueId()) == null }
            playersOffline.forEach { ProxyEventHandler.handleDisconnect(it.getUniqueId(), it.getName()) }
        }.delay(2L, TimeUnit.SECONDS).repeat(30L, TimeUnit.SECONDS).schedule()
    }

    private fun synchronizeOnlineCountTask() {
        proxyServer.scheduler.buildTask(this) {
            val service = CloudPlugin.instance.thisService()
            if (service.getOnlineCount() != proxyServer.playerCount) {
                service.setOnlineCount(proxyServer.playerCount)
                service.update()
            }
        }.repeat(30L, TimeUnit.SECONDS).schedule()
    }

    override fun shutdown() {
        proxyServer.commandManager.executeAsync(proxyServer.consoleCommandSource, "shutdown")
    }

    override fun getCloudPlayerManagerClass(): KClass<out ICloudPlayerManager> {
        return CloudPlayerManagerVelocity::class
    }

}