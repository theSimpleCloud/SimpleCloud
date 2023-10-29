package eu.thesimplecloud.module.support.lib.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.module.support.lib.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:02
 */

class TemplateFileCreator {

    fun create(): String {
        val stringBuilder = StringBuilder()
        CloudAPI.instance.getTemplateManager().getAllCachedObjects().forEach {
            stringBuilder.append("\n${getTemplateFile(it)}")
        }
        return stringBuilder.toString()
    }

    private fun getTemplateFile(template: ITemplate): String {
        return DumpFile::class.java.getResource("/temp/template.txt")!!.readText()
            .replace("%TEMPLATE_NAME%", template.getName())
            .replace("%INHERITED_TEMPLATES%", template.getInheritedTemplateNames().joinToString(", "))
            .replace("%MODULE_NAMES%", template.getModuleNamesToCopy().joinToString(", "))
    }

}