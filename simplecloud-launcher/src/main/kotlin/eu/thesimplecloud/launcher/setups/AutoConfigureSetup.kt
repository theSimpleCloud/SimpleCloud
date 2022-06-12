package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.impl.DefaultTemplate
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.console.setup.provider.BooleanSetupAnswerProvider

class AutoConfigureSetup : ISetup {

    @SetupQuestion(0, "Do you want to create a default Proxy-Group?", BooleanSetupAnswerProvider::class)
    fun setupProxy(answer: Boolean): Boolean {
        if (!answer) {
            return true
        }
        createProxyGroup()
        return true
    }

    @SetupQuestion(1, "Do you want to create a default Lobby-Group?", BooleanSetupAnswerProvider::class)
    fun setupLobby(answer: Boolean): Boolean {
        if (!answer) {
            return true
        }
        createLobbyGroup()
        return true
    }

    private fun createProxyGroup() {
        createTemplate(groupName = "Proxy")

        CloudAPI.instance.getCloudServiceGroupManager().createProxyGroup(
            "Proxy",
            "Proxy",
            512,
            64,
            1,
            5,
            true,
            static = false,
            percentToStartNewService = 75,
            wrapperName = null,
            permission = null,
            serviceVersion = CloudAPI.instance.getServiceVersionHandler().getServiceVersionByName("VELOCITY_3")!!,
            startPriority = 9,
            startPort = 25565
        )
    }


    private fun createLobbyGroup() {
        createTemplate(groupName = "Lobby")

        CloudAPI.instance.getCloudServiceGroupManager().createLobbyGroup(
            "Lobby",
            "Lobby",
            1024,
            32,
            1,
            5,
            true,
            static = false,
            percentToStartNewService = 75,
            wrapperName = null,
            priority = 0,
            permission = null,
            serviceVersion = CloudAPI.instance.getServiceVersionHandler().getServiceVersionByName("PAPER_1_18_2")!!,
            startPriority = 9,
            javaCommand = "java"
        )
    }

    private fun createTemplate(groupName: String) {
        val template = DefaultTemplate(groupName)
        CloudAPI.instance.getTemplateManager().update(template)
        template.getDirectory().mkdirs()
    }
}