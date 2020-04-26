package eu.thesimplecloud.api.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object ObjectSerializer {

    /**
     * Serialize given object into [String] using [ObjectOutputStream].
     * @param obj object to serialize
     * @see ObjectInputStream
     * @return the serialization result, empty string for _null_ input
     */
    fun <T> serialize(obj: T?): String {
        if (obj == null) {
            return ""
        }

        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(obj)
        oos.close()

        return baos.toString("ISO-8859-1")
    }

    /**
     * Deserialize given [String] using [ObjectInputStream].
     * @param string the string to deserialize
     * @return deserialized object, null, in case of error.
     */
    fun <T> deserialize(string: String): T? {
        if (string.isEmpty()) {
            return null
        }

        var bais = ByteArrayInputStream(string.toByteArray(charset("ISO-8859-1")))
        var ois = ObjectInputStream(bais)

        return ois.readObject() as T
    }

    fun <T> deserialize(string: String, clazz: Class<T>): T? = deserialize<T>(string)

}