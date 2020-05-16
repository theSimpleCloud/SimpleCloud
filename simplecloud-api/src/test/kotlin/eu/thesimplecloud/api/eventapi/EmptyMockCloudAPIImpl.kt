package eu.thesimplecloud.api.eventapi

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.message.IMessageChannelManager
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObjectManager
import eu.thesimplecloud.api.sync.list.manager.ISynchronizedObjectListManager
import eu.thesimplecloud.api.utils.IConnectedCloudProcess
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import org.mockito.Mockito.mock

open class EmptyMockCloudAPIImpl : CloudAPI() {
    override fun getCloudServiceGroupManager(): ICloudServiceGroupManager {
        return mock(ICloudServiceGroupManager::class.java)
    }

    override fun getCloudServiceManager(): ICloudServiceManager {
        return mock(ICloudServiceManager::class.java)
    }

    override fun getCloudPlayerManager(): ICloudPlayerManager {
        return mock(ICloudPlayerManager::class.java)
    }

    override fun getEventManager(): IEventManager {
        return mock(IEventManager::class.java)
    }

    override fun getCommandExecuteManager(): ICommandExecuteManager {
        return mock(ICommandExecuteManager::class.java)
    }

    override fun getSingleSynchronizedObjectManager(): ISingleSynchronizedObjectManager {
        return mock(ISingleSynchronizedObjectManager::class.java)
    }

    override fun getThisSidesCommunicationBootstrap(): ICommunicationBootstrap {
        return mock(ICommunicationBootstrap::class.java)
    }

    override fun getSynchronizedObjectListManager(): ISynchronizedObjectListManager {
        throw UnsupportedOperationException()
    }

    override fun getMessageChannelManager(): IMessageChannelManager {
        return mock(IMessageChannelManager::class.java)
    }

    override fun getThisSidesName(): String {
        return ""
    }

    override fun getThisSidesCloudProcess(): IConnectedCloudProcess {
        return mock(IConnectedCloudProcess::class.java)
    }

    override fun getThisSidesCloudModule(): ICloudModule {
        return mock(ICloudModule::class.java)
    }
}