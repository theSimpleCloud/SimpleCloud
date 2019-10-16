package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.lib.parser.string.IStringTypeParser

class CloudProxyGroupParser : IStringTypeParser<ICloudProxyGroup> {

    override fun allowedTypes(): List<Class<out ICloudProxyGroup>> = listOf(ICloudProxyGroup::class.java)

    override fun parse(string: String): ICloudProxyGroup? = CloudLib.instance.getCloudServiceGroupManager().getProxyGroup(string)
}