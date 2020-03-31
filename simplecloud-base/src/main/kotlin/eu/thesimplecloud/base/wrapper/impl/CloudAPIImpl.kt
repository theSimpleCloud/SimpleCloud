package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.`object`.ISynchronizedObjectManager
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.sync.list.manager.SynchronizedObjectListManager
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.client.impl.SynchronizedObjectManagerImpl
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap

class CloudAPIImpl : CloudAPI() {

    private val cloudServiceGroupManager = CloudServiceGroupManagerImpl()
    private val cloudServiceManager = CloudServiceManagerImpl()
    private val commandExecuteManager = CommandExecuteManagerImpl()
    private val cloudPlayerManager = CloudPlayerManagerImpl()
    private val eventManager = EventManagerImpl()
    private val synchronizedObjectManager = SynchronizedObjectManagerImpl(Wrapper.instance.communicationClient)
    private val synchronizedObjectListManager = SynchronizedObjectListManager()

    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager = this.cloudServiceGroupManager

    override fun getCloudServiceManager(): ICloudServiceManager = this.cloudServiceManager

    override fun getCloudPlayerManager(): ICloudPlayerManager = this.cloudPlayerManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getCommandExecuteManager(): ICommandExecuteManager = this.commandExecuteManager

    override fun getSynchronizedObjectManager(): ISynchronizedObjectManager = this.synchronizedObjectManager

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap = Wrapper.instance.communicationClient

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager = this.synchronizedObjectListManager

    override fun getThisSidesName(): String = Wrapper.instance.getThisWrapper().getName()
}