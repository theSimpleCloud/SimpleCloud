package eu.thesimplecloud.lib.utils

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IAuthenticatable : IConnectedClientValue {

    /**
     * Returns whether this [IConnectedClientValue] is authenticated
     */
    fun isAuthenticated(): Boolean

    /**
     * Sets this [IConnectedClientValue] authenticated
     */
    fun setAuthenticated(authenticated: Boolean)

}