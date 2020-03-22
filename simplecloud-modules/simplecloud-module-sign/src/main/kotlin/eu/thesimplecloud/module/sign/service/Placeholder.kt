package eu.thesimplecloud.module.sign.service

class Placeholder<T>(
        private val name: String,
        private val replaceValueFunction: (T) -> String
) {

    fun replacePlaceholder(type: T, message: String): String {
        return message.replace("%$name%", replaceValueFunction(type))
    }

}