package eu.thesimplecloud.api

import eu.thesimplecloud.api.template.ITemplateManager
import eu.thesimplecloud.api.template.impl.DefaultTemplateManager
import eu.thesimplecloud.api.wrapper.IWrapperManager
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperManager

abstract class CloudAPI : ICloudAPI {

    init {
        instance = this
    }

    private val wrapperManager: IWrapperManager = DefaultWrapperManager()
    private val templateManager: ITemplateManager = DefaultTemplateManager()

    override fun getWrapperManager(): IWrapperManager = this.wrapperManager

    override fun getTemplateManager(): ITemplateManager = this.templateManager

    fun isWindows(): Boolean = System.getProperty("os.name").toLowerCase().contains("windows")

    companion object {
        @JvmStatic
        lateinit var instance: CloudAPI
    }
}