package eu.thesimplecloud.launcher.external.module

import java.io.File

data class LoadedModuleFileContent(
        val file: File,
        val content: ModuleFileContent
)