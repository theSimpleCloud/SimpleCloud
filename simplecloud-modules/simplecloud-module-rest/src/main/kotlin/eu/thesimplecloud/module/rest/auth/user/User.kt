package eu.thesimplecloud.module.rest.auth.user

import eu.thesimplecloud.module.rest.annotation.WebExclude
import eu.thesimplecloud.module.rest.auth.user.permission.entity.PermissionEntity


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 18.06.2020
 * Time: 17:07
 */
data class User(
    val username: String,
    @WebExclude
    var password: String
) : PermissionEntity()