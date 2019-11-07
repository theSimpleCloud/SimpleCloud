package eu.thesimplecloud.lib

import eu.thesimplecloud.lib.eventapi.EventManager
import eu.thesimplecloud.lib.eventapi.IEventManager
import eu.thesimplecloud.lib.template.ITemplateManager
import eu.thesimplecloud.lib.template.impl.DefaultTemplateManager
import eu.thesimplecloud.lib.wrapper.IWrapperManager
import eu.thesimplecloud.lib.wrapper.impl.DefaultWrapperManager

abstract class CloudLib : ICloudLib {

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
        lateinit var instance: CloudLib
    }
}