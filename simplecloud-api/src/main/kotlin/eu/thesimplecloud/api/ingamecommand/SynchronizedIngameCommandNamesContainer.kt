package eu.thesimplecloud.api.ingamecommand

import eu.thesimplecloud.api.sync.`object`.ISingleSynchronizedObject

class SynchronizedIngameCommandNamesContainer(@Volatile var names: Collection<String> = HashSet()) : ISingleSynchronizedObject {

    override fun getName(): String = "simplecloud-ingamecommands"
}