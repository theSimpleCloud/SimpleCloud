package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.template.ITemplate

interface ITemplateCopier {

    fun copyTemplate(cloudService: ICloudService, template: ITemplate)

}