package eu.thesimplecloud.module.support.lib.creator

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.module.support.lib.DumpFile

/**
 * Created by MrManHD
 * Class create at 30.06.2023 21:02
 */

class WrapperFileCreator {

    fun create(): String {
        val stringBuilder = StringBuilder()
        CloudAPI.instance.getWrapperManager().getAllCachedObjects().forEach {
            stringBuilder.append("\n${getWrapperFile(it)}")
        }
        return stringBuilder.toString()
    }

    private fun getWrapperFile(wrapper: IWrapperInfo): String {
        return DumpFile::class.java.getResource("/temp/wrapper.txt")!!.readText()
            .replace("%ADDRESS%", wrapper.getHost())
            .replace("%WRAPPER_NAME%", wrapper.getName())
            .replace("%MAX_SIM_SERVICE%", wrapper.getMaxSimultaneouslyStartingServices().toString())
            .replace("%MAX_MEMORY%", wrapper.getMaxMemory().toString())
    }

}