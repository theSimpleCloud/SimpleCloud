package eu.thesimplecloud.base.wrapper.process.filehandler

import org.apache.commons.io.FileUtils
import java.io.File

class LoadedServiceVersion(
    private val srcFile: File,
    val fileNameToExecute: String
) {

    fun copyToDirectory(destDirectory: File) {
        if (this.srcFile.isDirectory) {
            copyDirectoryToDirectory(destDirectory)
            return
        }
        copyFilToDirectory(destDirectory)
    }

    private fun copyDirectoryToDirectory(destDirectory: File) {
        FileUtils.copyDirectory(srcFile, destDirectory)
    }

    private fun copyFilToDirectory(destDirectory: File) {
        val destFile = File(destDirectory, this.srcFile.name)
        FileUtils.copyFile(this.srcFile, destFile)
    }



}