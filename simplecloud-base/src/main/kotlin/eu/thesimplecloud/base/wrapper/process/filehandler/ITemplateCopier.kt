package eu.thesimplecloud.base.wrapper.process.filehandler

import eu.thesimplecloud.lib.depedency.DependenciesInformation
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.template.ITemplate
import java.io.File

interface ITemplateCopier {

    fun copyTemplate(cloudService: ICloudService, template: ITemplate)

    fun loadDependenciesAndInstall(serviceTmpDir: File): DependenciesInformation

}