package eu.thesimplecloud.base.manager.dump.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.manager.dump.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:02
 */

class WrapperDump(
    private val wrapperName: String,
    private val maxSimService: Int,
    private val maxMemory: Int
) {

    companion object {
        fun getWrappersDump(): List<WrapperDump> {
            val wrapperManager = CloudAPI.instance.getWrapperManager()
            return wrapperManager.getAllCachedObjects().map {
                WrapperDump(
                    it.getName(),
                    it.getMaxSimultaneouslyStartingServices(),
                    it.getMaxMemory()
                )
            }
        }

        fun createDumpFile(): String {
            val stringBuilder = StringBuilder()
            getWrappersDump().forEach {
                stringBuilder.append("\n${it.toDumpTxt()}")
            }
            return stringBuilder.toString()
        }
    }

    fun toDumpTxt(): String {
        return DumpFile::class.java.getResource("/dump/wrapper.txt")!!.readText()
            .replace("%WRAPPER_NAME%", this.wrapperName)
            .replace("%MAX_SIM_SERVICE%", this.maxSimService.toString())
            .replace("%MAX_MEMORY%", this.maxMemory.toString())
    }

}