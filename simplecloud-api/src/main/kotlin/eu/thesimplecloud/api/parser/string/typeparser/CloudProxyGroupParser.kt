package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup

class CloudProxyGroupParser : IStringTypeParser<ICloudProxyGroup> {

    override fun allowedTypes(): List<Class<out ICloudProxyGroup>> = listOf(ICloudProxyGroup::class.java)

    override fun parse(string: String): ICloudProxyGroup? = CloudAPI.instance.getCloudServiceGroupManager().getProxyGroupByName(string)
}