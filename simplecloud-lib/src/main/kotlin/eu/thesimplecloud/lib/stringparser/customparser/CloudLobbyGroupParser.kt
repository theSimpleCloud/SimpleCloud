package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import kotlin.reflect.KClass

class CloudLobbyGroupParser : ICustomTypeParser<ICloudLobbyGroup> {

    override fun allowedTypes(): List<Class<out ICloudLobbyGroup>> = listOf(ICloudLobbyGroup::class.java)

    override fun parse(string: String): ICloudLobbyGroup? = CloudLib.instance.getCloudServiceGroupManager().getLobbyGroup(string)
}