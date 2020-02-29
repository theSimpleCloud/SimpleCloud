package eu.thesimplecloud.module.permission.service


import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.core.TestSynchronizedListObject
import eu.thesimplecloud.plugin.server.CloudSpigotPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SpigotPluginMain : JavaPlugin() {


    override fun onEnable() {
        PermissionPool()
        val synchronizedObjectList = CloudAPI.instance.getSynchronizedObjectListManager().getSynchronizedObjectList("test")
        println("plugin startup ---------------------$synchronizedObjectList")
        val testSynchronizedListObject = TestSynchronizedListObject(5677, "Hallo1")
        synchronizedObjectList!!.update(testSynchronizedListObject)

        Bukkit.getScheduler().scheduleSyncDelayedTask(CloudSpigotPlugin.instance, {
            testSynchronizedListObject.testString = "Hallo2"
            synchronizedObjectList.update(testSynchronizedListObject)
        }, 20 * 10)

        Bukkit.getScheduler().scheduleSyncDelayedTask(CloudSpigotPlugin.instance, {
            synchronizedObjectList.remove(testSynchronizedListObject)
        }, 20 * 15)
    }

}