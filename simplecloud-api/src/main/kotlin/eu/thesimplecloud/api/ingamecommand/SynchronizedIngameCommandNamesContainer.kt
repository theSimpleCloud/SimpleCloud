package eu.thesimplecloud.api.ingamecommand

import eu.thesimplecloud.api.sync.`object`.ISynchronizedObject

class SynchronizedIngameCommandNamesContainer(@Volatile var names: Collection<String> = HashSet()) : ISynchronizedObject {

    override fun getName(): String = "simplecloud-ingamecommands"
}