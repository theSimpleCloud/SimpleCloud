package eu.thesimplecloud.api.property

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude


class Property<T : Any>(
        value: T
) : IProperty<T> {
    @GsonExclude
    @PacketExclude
    @Volatile
    var savedValue: T? = value

    val className = value::class.java.name

    @JsonIgnore
    private val valueAsString: String = JsonData.fromObject(value).getAsJsonString()

    @JsonIgnore
    @Synchronized
    override fun getValue(callerClassLoader: ClassLoader): T {
        if (savedValue == null) {
            val clazz = Class.forName(
                    className,
                    true,
                    callerClassLoader
            ) as Class<T>
            savedValue = JsonData.fromJsonString(valueAsString).getObject(clazz)
        }
        return savedValue!!
    }

    @JsonIgnore
    fun resetValue() {
        this.savedValue = null
    }

}