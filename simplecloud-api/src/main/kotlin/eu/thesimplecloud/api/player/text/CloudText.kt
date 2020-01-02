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