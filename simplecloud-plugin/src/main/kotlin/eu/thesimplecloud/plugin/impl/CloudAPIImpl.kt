package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.syncobject.ISynchronizedObjectManager
import eu.thesimplecloud.client.impl.SynchronizedObjectManagerImpl
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudAPIImpl : CloudAPI() {

    val cloudServiceManager = CloudServiceManagerImpl()
    val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    val commandExecuteManagerImpl = CommandExecuteManagerImpl()
    val cloudPlayerManager = CloudPlayerManagerImpl()
    val eventManager = EventManagerImpl()
    val synchronizedObjectManager = SynchronizedObjectManagerImpl(CloudPlugin.instance.communicationClient)

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManager(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManagerImpl

    override fun getSynchronizedObjectManager(): ISynchronizedObjectManager = this.synchronizedObjectManager

    override fun getThisSidesName(): String = CloudPlugin.instance.thisServiceName


}