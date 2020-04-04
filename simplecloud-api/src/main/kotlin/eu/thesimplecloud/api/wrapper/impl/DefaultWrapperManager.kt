package eu.thesimplecloud.api.wrapper.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.wrapper.WrapperUpdatedEvent
import eu.thesimplecloud.api.sync.list.AbstractSynchronizedObjectList
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.api.wrapper.IWrapperManager

open class DefaultWrapperManager : AbstractSynchronizedObjectList<IWrapperInfo>(), IWrapperManager {

    override fun getIdentificationName(): String {
        return "simplecloud-wrappers"
    }

    override fun update(value: IWrapperInfo, fromPacket: Boolean) {
        super.update(value, fromPacket)
        CloudAPI.instance.getEventManager().call(WrapperUpdatedEvent(getCachedObjectByUpdateValue(value)!!))
    }

    override fun getCachedObjectByUpdateValue(value: IWrapperInfo): IWrapperInfo? {
        return getWrapperByHost(value.getHost())
    }


}