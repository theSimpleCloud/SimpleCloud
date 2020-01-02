package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.api.network.packets.wrapper.PacketIOUpdateWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager

class WrapperManagerImpl : DefaultWrapperManager() {

    override fun updateWrapper(wrapper: IWrapperInfo) {
        super.updateWrapper(wrapper)
        Manager.instance.wrapperFileHandler.save(wrapper)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateWrapperInfo(wrapper))
    }

}