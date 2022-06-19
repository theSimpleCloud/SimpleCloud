package eu.thesimplecloud.plugin.proxy.bungee

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.06.22
 * Time: 15:04
 */

fun Component.toBaseComponent(): BaseComponent {
    return BungeeComponentSerializer.get().serialize(this)[0]
}