package eu.thesimplecloud.api.utils

import eu.thesimplecloud.api.client.NetworkComponentReference
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.screen.ICommandExecutable

interface INetworkComponent : IAuthenticatable, ICommandExecutable {

    companion object {
        @JvmStatic
        val MANAGER_COMPONENT = object : INetworkComponent {
            override fun getNetworkComponentType(): NetworkComponentType {
                return NetworkComponentType.MANAGER
            }

            override fun isAuthenticated(): Boolean {
                return true
            }

            override fun setAuthenticated(authenticated: Boolean) {
                throw UnsupportedOperationException("Cannot set authenticated state of the manager")
            }

            override fun executeCommand(command: String) {
                throw UnsupportedOperationException("Cannot execute a command on the manager")
            }

            override fun getName(): String {
                return "Manager"
            }

            override fun toNetworkComponentReference(): NetworkComponentReference {
                return NetworkComponentReference.MANAGER_COMPONENT_REFERENCE
            }
        }
    }

    /**
     * Returns the [NetworkComponentType] of this process
     */
    fun getNetworkComponentType(): NetworkComponentType

    /**
     * Returns the [NetworkComponentReference] of this process.
     */
    fun toNetworkComponentReference(): NetworkComponentReference {
        return NetworkComponentReference(getNetworkComponentType(), getName())
    }


}