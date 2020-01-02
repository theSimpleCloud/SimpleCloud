package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.lib.parser.string.IStringTypeParser

class CloudLobbyGroupParser : IStringTypeParser<ICloudLobbyGroup> {

    override fun allowedTypes(): List<Class<out ICloudLobbyGroup>> = listOf(ICloudLobbyGroup::class.java)

    override fun parse(string: String): ICloudLobbyGroup? = CloudLib.instance.getCloudServiceGroupManager().getLobbyGroupByName(string)
}