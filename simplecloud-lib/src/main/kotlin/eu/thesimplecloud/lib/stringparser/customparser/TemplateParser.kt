package eu.thesimplecloud.lib.stringparser.customparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.stringparser.ICustomTypeParser
import eu.thesimplecloud.lib.template.ITemplate

class TemplateParser : ICustomTypeParser<ITemplate> {

    override fun allowedTypes(): List<Class<out ITemplate>> = listOf(ITemplate::class.java)

    override fun parse(string: String): ITemplate? = CloudLib.instance.getTemplateManager().getTemplate(string)
}