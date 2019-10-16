package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.parser.string.IStringTypeParser
import eu.thesimplecloud.lib.wrapper.IWrapperInfo

class WrapperInfoParser : IStringTypeParser<IWrapperInfo> {

    override fun allowedTypes(): List<Class<out IWrapperInfo>> = listOf(IWrapperInfo::class.java)

    override fun parse(string: String): IWrapperInfo? = CloudLib.instance.getWrapperManager().getWrapperByName(string)
}