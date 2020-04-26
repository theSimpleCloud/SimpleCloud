package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.service.ICloudService

class CloudServiceParser : IStringTypeParser<ICloudService> {

    override fun allowedTypes(): List<Class<out ICloudService>> = listOf(ICloudService::class.java)

    override fun parse(string: String): ICloudService? = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(string)
}