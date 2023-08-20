package eu.thesimplecloud.module.npc.lib.extension

fun String.translateColorCodesFromString(): String {
    var string = this
    string = string.replace("&0".toRegex(), "§0")
    string = string.replace("&1".toRegex(), "§1")
    string = string.replace("&2".toRegex(), "§2")
    string = string.replace("&3".toRegex(), "§3")
    string = string.replace("&4".toRegex(), "§4")
    string = string.replace("&5".toRegex(), "§5")
    string = string.replace("&6".toRegex(), "§6")
    string = string.replace("&7".toRegex(), "§7")
    string = string.replace("&8".toRegex(), "§8")
    string = string.replace("&9".toRegex(), "§9")
    string = string.replace("&a".toRegex(), "§a")
    string = string.replace("&b".toRegex(), "§b")
    string = string.replace("&c".toRegex(), "§c")
    string = string.replace("&d".toRegex(), "§d")
    string = string.replace("&e".toRegex(), "§e")
    string = string.replace("&f".toRegex(), "§f")
    string = string.replace("&k".toRegex(), "§k")
    string = string.replace("&m".toRegex(), "§m")
    string = string.replace("&n".toRegex(), "§n")
    string = string.replace("&l".toRegex(), "§l")
    string = string.replace("&o".toRegex(), "§o")
    string = string.replace("&r".toRegex(), "§r")
    return string
}