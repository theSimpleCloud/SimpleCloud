package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import kotlin.reflect.KClass

class CloudServerGroupParser : ICustomTypeParser<ICloudServerGroup> {

    override fun allowedTypes(): List<KClass<out ICloudServerGroup>> = listOf(ICloudServerGroup::class)

    override fun parse(string: String): ICloudServerGroup? = CloudLib.instance.getCloudServiceGroupManager().getServerGroup(string)
}