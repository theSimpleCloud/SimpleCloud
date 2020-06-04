/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.player.text

class CloudText(val text: String) {


    /**
     * The text shall be shown when hovering over the [text]
     */
    var hover: String? = null
        private set

    /**
     * The action content to perform when the text is clicked.
     */
    var click: String? = null
        private set

    /**
     * The action that will be performed when the text is clicked.
     */
    var clickEventType: ClickEventType? = null
        private set

    /**
     * The [CloudText] to append to this text.
     */
    var appendedCloudText: CloudText? = null
        private set

    fun addHover(hover: String): CloudText {
        this.hover = hover
        return this
    }

    fun addClickEvent(clickEventType: ClickEventType, value: String): CloudText {
        this.clickEventType = clickEventType
        click = value
        return this
    }

    fun setAppendedCloudText(cloudText: CloudText): CloudText {
        this.appendedCloudText = cloudText
        return this
    }

    enum class ClickEventType {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }

}