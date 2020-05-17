package eu.thesimplecloud.api.utils

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

interface IAuthenticatable {

    /**
     * Returns whether this [IConnectedClientValue] is authenticated
     */
    fun isAuthenticated(): Boolean

    /**
     * Sets this [IConnectedClientValue] authenticated
     */
    fun setAuthenticated(authenticated: Boolean)

}