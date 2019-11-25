package eu.thesimplecloud.lib.player.text

class CloudText (val text: String){


    private var hover: String? = null
    private var click: String? = null
    private var clickEventType: ClickEventType? = null
    private var appendedCloudText: CloudText? = null

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