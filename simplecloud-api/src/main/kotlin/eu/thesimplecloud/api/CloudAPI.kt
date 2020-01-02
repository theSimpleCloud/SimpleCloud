package eu.thesimplecloud.api

import eu.thesimplecloud.api.eventapi.EventManager
import eu.thesimplecloud.api.eventapi.IEventManager
import eu.thesimplecloud.api.template.ITemplateManager
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager

abstract class CloudAPI : ICloudAPI {

    init {
        instance = this
    }

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()
    private val eventManager: IEventManager = EventManager()
    private val templateManager: ITemplateManager = DefaultTemplateManager()

    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getEventManager(): IEventManager = this.eventManager

    override fun getTemplateManager(): ITemplateManager = this.templateManager

    fun isWindows(): Boolean = System.getProperty("os.name").toLowerCase().contains("windows")

    companion object {
        @JvmStatic
        lateinit var instance: CloudAPI
    }
}