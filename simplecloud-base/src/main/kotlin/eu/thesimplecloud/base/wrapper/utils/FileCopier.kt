package eu.thesimplecloud.base.wrapper.utils

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class FileCopier {

    companion object {
        /**
         * Copies a file outside this jar
         */
        fun copyFileOutOfJar(fileDestination: File, filePathToCopy: String) {
            val stream = this.javaClass.getResourceAsStream(filePathToCopy)
            val parent = fileDestination.parentFile
            parent?.mkdirs()
            if (File(filePathToCopy).exists()) {
                return
            }
            try {
                fileDestination.createNewFile()
                FileUtils.copyInputStreamToFile(stream, fileDestination)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
    }

}