package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

class WrapperRamWriter(private val wrapperRam: Map<String, String>) {

    private val directory = File("storage/wrappers")


    fun write() {
        val wrapperToRam = wrapperRam.filter { it.value.toIntOrNull() != null }.map { it.key to it.value.toInt() }
        wrapperToRam.forEach {
            writeRamToFile(it.first, it.second)
        }
    }

    private fun writeRamToFile(wrapperName: String, ram: Int) {
        val wrapperFile = File(directory, "$wrapperName.json")
        if (!wrapperFile.exists()) {
            return
        }
        val wrapperInfo = JsonLib.fromJsonFile(wrapperFile)!!.getObject(DefaultWrapperInfo::class.java)
        val newWrapperInfo = DefaultWrapperInfo(
            wrapperInfo.getName(),
            wrapperInfo.getHost(),
            wrapperInfo.getMaxSimultaneouslyStartingServices(),
            ram
        )
        JsonLib.fromObject(newWrapperInfo).saveAsFile(wrapperFile)
    }

}