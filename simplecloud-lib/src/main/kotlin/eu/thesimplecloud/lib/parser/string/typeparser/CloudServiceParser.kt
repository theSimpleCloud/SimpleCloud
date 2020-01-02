package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.parser.string.IStringTypeParser

class CloudServiceParser : IStringTypeParser<ICloudService> {

    override fun allowedTypes(): List<Class<out ICloudService>> = listOf(ICloudService::class.java)

    override fun parse(string: String): ICloudService? = CloudLib.instance.getCloudServiceManger().getCloudServiceByName(string)
}