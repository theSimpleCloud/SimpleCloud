package eu.thesimplecloud.module.permission.player

import com.fasterxml.jackson.annotation.JsonIgnore

class PlayerPermissionGroupInfo(val permissionGroupName: String, val timeoutTimestamp: Long) {

    @JsonIgnore
    fun isExpired(): Boolean = (System.currentTimeMillis() > timeoutTimestamp) && timeoutTimestamp != -1L
}