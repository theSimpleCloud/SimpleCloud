package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

class CloudServiceGroupParser : IStringTypeParser<ICloudServiceGroup> {

    override fun allowedTypes(): List<Class<out ICloudServiceGroup>> = listOf(ICloudServiceGroup::class.java)

    override fun parse(string: String): ICloudServiceGroup? = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(string)
}