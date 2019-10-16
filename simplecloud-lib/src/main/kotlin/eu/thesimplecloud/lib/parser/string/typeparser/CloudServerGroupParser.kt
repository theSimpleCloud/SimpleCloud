package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.lib.parser.string.IStringTypeParser

class CloudServerGroupParser : IStringTypeParser<ICloudServerGroup> {

    override fun allowedTypes(): List<Class<out ICloudServerGroup>> = listOf(ICloudServerGroup::class.java)

    override fun parse(string: String): ICloudServerGroup? = CloudLib.instance.getCloudServiceGroupManager().getServerGroup(string)
}