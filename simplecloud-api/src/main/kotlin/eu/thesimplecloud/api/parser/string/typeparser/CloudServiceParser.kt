package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.parser.string.IStringTypeParser

class CloudServiceParser : IStringTypeParser<ICloudService> {

    override fun allowedTypes(): List<Class<out ICloudService>> = listOf(ICloudService::class.java)

    override fun parse(string: String): ICloudService? = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(string)
}