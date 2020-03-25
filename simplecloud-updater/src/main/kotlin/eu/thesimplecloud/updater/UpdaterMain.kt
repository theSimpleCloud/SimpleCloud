package eu.thesimplecloud.updater

import java.io.File


/**
 * First argument: delay to wait before replacing
 * Second argument: File to be replaced
 * Third argument: File to replace with
 */
fun main(args: Array<String>) {
    val timeToWait = args[0].toLong()
    val fileToBeReplaced = File(args[1])
    val fileToReplaceWith = File(args[2])
    Thread.sleep(timeToWait)
    while (fileToBeReplaced.exists())
        try {
            fileToBeReplaced.delete()
        } catch (e: Exception) {
            Thread.sleep(100)
        }
    fileToReplaceWith.copyTo(fileToBeReplaced)
    fileToReplaceWith.delete()
}