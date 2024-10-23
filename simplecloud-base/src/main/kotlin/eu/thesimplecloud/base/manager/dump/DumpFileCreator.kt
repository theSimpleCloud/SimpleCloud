package eu.thesimplecloud.base.manager.dump

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MrManHD
 * Class create at 25.06.2023 20:57
 */

class DumpFileCreator(
    private val timestamp: Long = System.currentTimeMillis()
) {

    fun create(): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        return DumpFile(simpleDateFormat.format(Date(this.timestamp))).fileString
    }

}