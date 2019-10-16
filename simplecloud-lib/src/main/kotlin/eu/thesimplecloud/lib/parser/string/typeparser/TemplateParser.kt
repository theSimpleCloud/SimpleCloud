package eu.thesimplecloud.lib.parser.string.typeparser

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.parser.string.IStringTypeParser
import eu.thesimplecloud.lib.template.ITemplate

class TemplateParser : IStringTypeParser<ITemplate> {

    override fun allowedTypes(): List<Class<out ITemplate>> = listOf(ITemplate::class.java)

    override fun parse(string: String): ITemplate? = CloudLib.instance.getTemplateManager().getTemplate(string)
}