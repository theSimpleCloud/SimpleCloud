package eu.thesimplecloud.lib.wrapper.impl

import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo

data class DefaultWrapperInfo(
        private val name: String,
        private val host: String,
        private var maxSimultaneouslyStartingServices: Int,
        private var maxMemory: Int
) : IWritableWrapperInfo {

    @GsonExclude
    private var authenticated = false

    @GsonExclude
    private var usedMemory: Int = 0

    override fun setUsedMemory(memory: Int) {
        this.usedMemory = memory
    }

    override fun setMaxSimultaneouslyStartingServices(amount: Int) {
        this.maxSimultaneouslyStartingServices = amount
    }

    override fun setMaxMemory(memory: Int) {
        this.maxMemory = memory
    }

    override fun setAuthenticated(boolean: Boolean) {
        this.authenticated = boolean
    }

    override fun getName(): String = this.name

    override fun getHost(): String = this.host

    override fun getMaxSimultaneouslyStartingServices(): Int = this.maxSimultaneouslyStartingServices

    override fun getUsedMemory(): Int = this.usedMemory

    override fun getMaxMemory(): Int = this.maxMemory

    override fun isAuthenticated(): Boolean = this.authenticated
}