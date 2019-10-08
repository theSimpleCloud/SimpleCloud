package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import kotlin.reflect.KClass

class CloudServiceParser : ICustomTypeParser<ICloudService> {

    override fun allowedTypes(): List<KClass<out ICloudService>> = listOf(ICloudService::class)

    override fun parse(string: String): ICloudService? = CloudLib.instance.getCloudServiceManger().getCloudService(string)
}