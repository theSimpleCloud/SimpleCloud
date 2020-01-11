package eu.thesimplecloud.launcher.external.module

/**
 * Shows on which services the module shall be copied as plugin.
 */
enum class ModuleCopyType {

    /**
     * The module will not be copied to any service.
     */
    NONE,
    /**
     * The module will be copied to all services.
     */
    ALL,
    /**
     * The module will be copied to all proxy services.
     */
    PROXY,
    /**
     * The modules will be copied to all minecraft servers.
     */
    SERVER,
    /**
     * The module will be copied to all lobby services.
     */
    LOBBY


}