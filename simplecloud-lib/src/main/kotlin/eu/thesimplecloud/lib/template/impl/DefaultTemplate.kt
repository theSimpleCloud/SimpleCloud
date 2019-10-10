package eu.thesimplecloud.lib.template.impl

import eu.thesimplecloud.lib.template.ITemplate

data class DefaultTemplate(private val name: String) : ITemplate {

    override fun getName(): String = this.name
}