package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObjectManager
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.sync.list.manager.SynchronizedObjectListManager
import eu.thesimplecloud.client.impl.SingleSynchronizedObjectManagerImpl
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudAPIImpl : CloudAPI() {

    private val cloudServiceManager = CloudServiceManagerImpl()
    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val commandExecuteManagerImpl = CommandExecuteManagerImpl()
    private val cloudPlayerManager = CloudPlayerManagerImpl()
    private val eventManager = EventManagerImpl()
    private val synchronizedObjectManager = SingleSynchronizedObjectManagerImpl(CloudPlugin.instance.communicationClient)
    private val synchronizedObjectListManager = SynchronizedObjectListManager()

    init {
        getSynchronizedObjectListManager().registerSynchronizedObjectList(getWrapperManager(), false)
    }

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManager(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManagerImpl

    override fun getSingleSynchronizedObjectManager(): ISingleSynchronizedObjectManager = this.synchronizedObjectManager

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap = CloudPlugin.instance.communicationClient

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager = this.synchronizedObjectListManager

    override fun getThisSidesName(): String = CloudPlugin.instance.thisServiceName

    override fun getThisSidesCloudModule(): ICloudModule = CloudPlugin.instance


}