package eu.thesimplecloud.api.property

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude


class Property<T : Any>(
        value: T
) {
    @GsonExclude
    @PacketExclude
    @Volatile
    var savedValue: T? = value

    val className = value::class.java.name

    @JsonIgnore
    private val valueAsString: String = JsonData.fromObject(value).getAsJsonString()

    @JsonIgnore
    @Synchronized
    fun getValue(classLoader: ClassLoader): T {

        if (savedValue == null) {
            val clazz = Class.forName(className, true, classLoader) as Class<T>
            savedValue = JsonData.fromJsonString(valueAsString).getObject(clazz)
        }
        try {
            return savedValue!!
        } catch (e: Exception) {
            throw e
        }
    }

    @JsonIgnore
    fun getValue(): T {
        return getValue(this::class.java.classLoader)
    }

    @JsonIgnore
    fun resetValue() {
        this.savedValue = null
    }

    @JsonIgnore
    fun loadValue(classLoader: ClassLoader) {
        if (this.savedValue == null) {
            runCatching { getValue(classLoader) }
        }
    }

}