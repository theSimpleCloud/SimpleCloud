package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import kotlin.reflect.KClass

class CloudServiceGroupParser : ICustomTypeParser<ICloudServiceGroup> {

    override fun allowedTypes(): List<KClass<out ICloudServiceGroup>> = listOf(ICloudServiceGroup::class)

    override fun parse(string: String): ICloudServiceGroup? = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(string)
}