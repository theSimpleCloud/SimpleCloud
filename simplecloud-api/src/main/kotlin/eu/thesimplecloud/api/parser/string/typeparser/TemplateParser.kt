package eu.thesimplecloud.api.parser.string.typeparser

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.parser.string.IStringTypeParser
import eu.thesimplecloud.api.template.ITemplate

class TemplateParser : IStringTypeParser<ITemplate> {

    override fun allowedTypes(): List<Class<out ITemplate>> = listOf(ITemplate::class.java)

    override fun parse(string: String): ITemplate? = CloudAPI.instance.getTemplateManager().getTemplate(string)
}