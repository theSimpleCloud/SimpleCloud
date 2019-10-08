package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import kotlin.reflect.KClass

class WrapperInfoParser : ICustomTypeParser<IWrapperInfo> {

    override fun allowedTypes(): List<Class<out IWrapperInfo>> = listOf(IWrapperInfo::class.java)

    override fun parse(string: String): IWrapperInfo? = CloudLib.instance.getWrapperManager().getWrapperByName(string)
}