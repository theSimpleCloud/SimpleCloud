package eu.thesimplecloud.lib.utils

import java.lang.reflect.Field

fun Class<*>.getAllFieldsFromClassAndSubClasses(): Set<Field> {
    val fields = this.declaredFields.toSet()
    if (this.superclass != null)
        return fields.union(this.superclass.getAllFieldsFromClassAndSubClasses())
    return fields
}