package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.wrapper.IWrapperInfo

class WrapperInfoParser : IStringTypeParser<IWrapperInfo> {

    override fun allowedTypes(): List<Class<out IWrapperInfo>> = listOf(IWrapperInfo::class.java)

    override fun parse(string: String): IWrapperInfo? = CloudAPI.instance.getWrapperManager().getWrapperByName(string)
}