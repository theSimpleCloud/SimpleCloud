package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager
import eu.thesimplecloud.base.manager.startup.Manager

class WrapperManagerImpl : DefaultWrapperManager() {

    override fun update(value: IWrapperInfo, fromPacket: Boolean) {
        super.update(value, fromPacket)
        Manager.instance.wrapperFileHandler.save(value)
    }

}