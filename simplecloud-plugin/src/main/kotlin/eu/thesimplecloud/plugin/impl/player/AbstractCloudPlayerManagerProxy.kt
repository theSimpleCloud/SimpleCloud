package eu.thesimplecloud.plugin.impl.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Date: 18.06.22
 * Time: 17:52
 * @author Frederick Baier
 *
 */
abstract class AbstractCloudPlayerManagerProxy : AbstractServiceCloudPlayerManager() {

    protected fun getHexColorComponent(message: String): TextComponent {
        return Component.textOfChildren(MiniMessage.miniMessage().deserialize(message))
    }

}