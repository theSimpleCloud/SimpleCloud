package eu.thesimplecloud.module.prefix.config

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.utils.Nameable

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 13:36
 */
data class Config(
    val chatFormat: String = "%PLAYER% §8» §7%MESSAGE%",
    val informationList: List<TablistInformation> = listOf(TablistInformation())
) {
    companion object {

        @Volatile
        private var property: IProperty<Config>? = null

        fun getConfig(): Config {
            if (this.property == null) {
                this.property = CloudAPI.instance.getGlobalPropertyHolder().requestProperty<Config>("prefix-config").getBlocking()
            }
            return this.property!!.getValue()
        }

    }

}