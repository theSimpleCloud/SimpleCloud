package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.template.ITemplate

interface ITemplateCopier {

    fun copyTemplate(cloudService: ICloudService, template: ITemplate)

}