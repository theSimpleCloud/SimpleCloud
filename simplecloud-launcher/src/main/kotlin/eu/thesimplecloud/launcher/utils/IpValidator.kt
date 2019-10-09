package eu.thesimplecloud.launcher.utils

import java.util.regex.Pattern

class IpValidator {

    private val IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"

    private val pattern = Pattern.compile(IPADDRESS_PATTERN)

    fun validate(ip: String): Boolean {
        val matcher = pattern.matcher(ip)
        return matcher.matches()
    }

}