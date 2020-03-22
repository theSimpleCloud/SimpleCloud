package eu.thesimplecloud.module.sign.lib

import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude

class SignLayout(val name: String, private val frames: List<SignFrame>) {

    @GsonExclude
    private var currentFrameIndex = 0

    fun getCurrentFrame(): SignFrame {
        return frames.getOrNull(currentFrameIndex) ?: SignFrame(arrayOf("", "empty layout", "", ""))
    }

    fun nextFrame() {
        currentFrameIndex++
        if (currentFrameIndex > this.frames.size - 1) currentFrameIndex = 0
    }

}