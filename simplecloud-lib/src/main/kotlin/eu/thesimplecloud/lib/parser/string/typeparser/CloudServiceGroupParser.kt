package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.parser.string.IStringTypeParser

class CloudServiceGroupParser : IStringTypeParser<ICloudServiceGroup> {

    override fun allowedTypes(): List<Class<out ICloudServiceGroup>> = listOf(ICloudServiceGroup::class.java)

    override fun parse(string: String): ICloudServiceGroup? = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(string)
}