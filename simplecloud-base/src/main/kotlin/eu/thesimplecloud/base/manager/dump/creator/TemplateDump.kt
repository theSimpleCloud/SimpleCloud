package eu.thesimplecloud.base.manager.dump.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.base.manager.dump.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:02
 */

class TemplateDump(
    private val templateName: String,
    private val inheritedTemplates: Set<String>,
    private val moduleNames: Set<String>
) {

    companion object {
        fun getTemplatesDump(): List<TemplateDump> {
            val templateManager = CloudAPI.instance.getTemplateManager()
            return templateManager.getAllCachedObjects().map {
                TemplateDump(
                    it.getName(),
                    it.getInheritedTemplateNames(),
                    it.getModuleNamesToCopy(),
                )
            }
        }

        fun createDumpFile(): String {
            val stringBuilder = StringBuilder()
            getTemplatesDump().forEach {
                stringBuilder.append("\n${it.toDumpTxt()}")
            }
            return stringBuilder.toString()
        }
    }

    fun toDumpTxt(): String {
        return DumpFile::class.java.getResource("/dump/template.txt")!!.readText()
            .replace("%TEMPLATE_NAME%", this.templateName)
            .replace("%INHERITED_TEMPLATES%", this.inheritedTemplates.joinToString(", "))
            .replace("%MODULE_NAMES%", this.moduleNames.joinToString(", "))
    }

}