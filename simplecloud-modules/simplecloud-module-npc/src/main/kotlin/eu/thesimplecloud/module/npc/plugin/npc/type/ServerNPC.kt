package eu.thesimplecloud.module.npc.plugin.npc.type

interface ServerNPC {

    fun onSetup()

    fun onRemove()

    fun getEntityHigh(): Double
}