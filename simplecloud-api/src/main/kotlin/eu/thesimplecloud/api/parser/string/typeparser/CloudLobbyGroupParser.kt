package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup

class CloudLobbyGroupParser : IStringTypeParser<ICloudLobbyGroup> {

    override fun allowedTypes(): List<Class<out ICloudLobbyGroup>> = listOf(ICloudLobbyGroup::class.java)

    override fun parse(string: String): ICloudLobbyGroup? = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroupByName(string)
}