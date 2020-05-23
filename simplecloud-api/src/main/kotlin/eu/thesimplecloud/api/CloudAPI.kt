package eu.thesimplecloud.api

import eu.thesimplecloud.api.cachelist.manager.CacheListManager
import eu.thesimplecloud.api.cachelist.manager.ICacheListManager
import eu.thesimplecloud.api.message.IMessageChannelManager
import eu.thesimplecloud.api.message.MessageChannelManager
import eu.thesimplecloud.api.template.ITemplateManager
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager

abstract class CloudAPI : ICloudAPI {

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()
    private val templateManager: ITemplateManager = DefaultTemplateManager()
    private val messageChannelManager: IMessageChannelManager = MessageChannelManager()
    private val cacheListManager: ICacheListManager = CacheListManager()

    init {
        instance = this
    }



    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getTemplateManager(): ITemplateManager = this.templateManager

    override fun getMessageChannelManager(): IMessageChannelManager = this.messageChannelManager

    override fun getCacheListManager(): ICacheListManager = this.cacheListManager

    companion object {
        @JvmStatic
        lateinit var instance: ICloudAPI
            private set
    }
}