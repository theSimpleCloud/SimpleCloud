package eu.thesimplecloud.lib.bootstrap

interface ICloudBootstrapGetter {

    /**
     * Returns the [ICloudBootstrap].
     * Depending on where this object was instantiated it will return a Manager, Wrapper or a CloudPlugin
     */
    fun getCloudBootstrap(): ICloudBootstrap
}