package eu.thesimplecloud.api.property

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData


class Property<T : Any>(
        value: T
) {
    @GsonExclude
    @Volatile
    var savedValue: T? = value

    val className = value::class.java.name

    @JsonIgnore
    private val valueAsString: String = JsonData.fromObject(value).getAsJsonString()

    @JsonIgnore
    fun getValue(classLoader: ClassLoader): T {
        savedValue?.let {
            savedValue = JsonData.fromJsonString(valueAsString).getObject(Class.forName(className, true, classLoader) as Class<T>)
        }
        return savedValue!!
    }

    @JsonIgnore
    fun getValue(): T {
        return getValue(this::class.java.classLoader)
    }

    @JsonIgnore
    fun resetValue() {
        this.savedValue = null
    }

}