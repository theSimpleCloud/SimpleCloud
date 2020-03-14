package eu.thesimplecloud.module.permission.permission

data class Permission(val permissionString: String, val timeoutTimestamp: Long, val active: Boolean) {

    fun isExpired(): Boolean = (System.currentTimeMillis() > timeoutTimestamp) && timeoutTimestamp != -1L

}