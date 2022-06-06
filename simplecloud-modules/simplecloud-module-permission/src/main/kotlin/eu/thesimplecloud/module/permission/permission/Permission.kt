/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.permission.permission


data class Permission(val permissionString: String, val timeoutTimestamp: Long, val active: Boolean) {

    fun isExpired(): Boolean = (System.currentTimeMillis() > timeoutTimestamp) && timeoutTimestamp != -1L


    fun matches(permission: String): Boolean {
        return if (isExpired()) {
            false
        } else {
            return matchesPermission(permission)
        }
    }

    private fun matchesPermission(permission: String): Boolean {
        val endsWithStar = this.permissionString.endsWith(".*")
        return if (endsWithStar) {
            matchesWithStarAtTheEnd(permission)
        } else {
            isPermissionToCheckEqualToThisPermission(permission)
        }
    }

    private fun isPermissionToCheckEqualToThisPermission(permission: String): Boolean {
        return permission == this.permissionString
    }

    private fun matchesWithStarAtTheEnd(permission: String): Boolean {
        val checkPermissionComponents = permission.split(".")
        val thisPermissionComponents = this.permissionString.split(".")

        val isPermissionToCheckLongEnough = checkPermissionComponents.size >= thisPermissionComponents.size
        return if (isPermissionToCheckLongEnough) {
            areAllPermissionComponentsEqual(checkPermissionComponents, thisPermissionComponents)
        } else {
            false
        }
    }

    private fun areAllPermissionComponentsEqual(
        checkPermissionComponents: List<String>,
        thisPermissionComponents: List<String>
    ): Boolean {
        val thisPermissionComponentsWithoutStar = thisPermissionComponents.dropLast(1)
        val checkComponentsWithLimit = checkPermissionComponents.take(thisPermissionComponentsWithoutStar.size)
        return checkComponentsWithLimit == thisPermissionComponentsWithoutStar
    }

}