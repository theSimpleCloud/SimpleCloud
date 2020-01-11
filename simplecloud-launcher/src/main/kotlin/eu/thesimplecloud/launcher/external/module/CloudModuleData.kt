package eu.thesimplecloud.launcher.external.module

import eu.thesimplecloud.api.external.ICloudModule
import java.io.File

class CloudModuleData(
        val cloudModule: ICloudModule,
        val file: File,
        val cloudModuleFileContent: CloudModuleFileContent
) {
}