package eu.thesimplecloud.lib.utils

fun Class<out Enum<*>>.getEnumValues(): List<String> {
    val method = this.getMethod("values")
    val values = method.invoke(null) as Array<Enum<*>>
    return values.map { it.name }
}

fun <T : Enum<*>> Class<out T>.enumValueOf(string: String): T {
    val method = this.getMethod("valueOf", String::class.java)
    return method.invoke(null, string) as T
}