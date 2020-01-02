package eu.thesimplecloud.lib.eventapi

/**
 * Represents a class that is can be cancelled.
 */
interface ICancellable {

    /**
     * Returns whether this action was cancelled.
     */
    fun isCancelled(): Boolean

    /**
     * Sets the cancelled state
     */
    fun setCancelled(cancel: Boolean)

}