package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.template.ITemplate

interface ITemplateCopier {

    fun copyTemplate(cloudService: ICloudService, template: ITemplate)

    //fun loadDependenciesAndInstall(serviceTmpDir: File)

}