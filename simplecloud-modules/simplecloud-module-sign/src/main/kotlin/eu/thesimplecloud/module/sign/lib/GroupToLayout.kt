package eu.thesimplecloud.module.sign.lib

class GroupToLayout {

    private val groupToLayout = HashMap<String, String>()

    fun putGroupToLayout(groupName: String, layoutName: String) {
        this.groupToLayout[groupName] = layoutName
    }

    fun getLayoutByGroupName(groupName: String): SignLayout? {
        val signLayoutName = this.groupToLayout[groupName] ?: return null
        return SignModuleConfig.INSTANCE.getSignLayoutByName(signLayoutName)
    }

}