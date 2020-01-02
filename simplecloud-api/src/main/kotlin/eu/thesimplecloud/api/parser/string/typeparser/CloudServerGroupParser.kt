package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.api.parser.string.IStringTypeParser

class CloudServerGroupParser : IStringTypeParser<ICloudServerGroup> {

    override fun allowedTypes(): List<Class<out ICloudServerGroup>> = listOf(ICloudServerGroup::class.java)

    override fun parse(string: String): ICloudServerGroup? = CloudAPI.instance.getCloudServiceGroupManager().getServerGroupByName(string)
}