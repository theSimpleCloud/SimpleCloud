package eu.thesimplecloud.module.permission.service.spigot.util

import org.bukkit.Bukkit

class ReflectionUtils {

    companion object {
        fun reflectCraftClazz(suffix: String): Class<*>? {
            try {
                val version = Bukkit.getServer().javaClass.getPackage().name.split(".").toTypedArray()[3]
                return Class.forName("org.bukkit.craftbukkit.$version$suffix")
            } catch (ex: Exception) {
                ex.printStackTrace()
                try {
                    return Class.forName("org.bukkit.craftbukkit$suffix")
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            return null
        }

    }

}