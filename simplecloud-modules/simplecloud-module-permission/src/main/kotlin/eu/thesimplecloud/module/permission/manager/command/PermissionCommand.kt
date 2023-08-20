/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.module.permission.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.CloudPlayerCommandSuggestionProvider
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.manager.PermissionModule
import eu.thesimplecloud.module.permission.permission.Permission
import eu.thesimplecloud.module.permission.player.IPermissionPlayer
import eu.thesimplecloud.module.permission.player.PlayerPermissionGroupInfo
import eu.thesimplecloud.module.permission.player.getPermissionPlayer
import java.util.concurrent.TimeUnit

@Command("perms", CommandType.CONSOLE_AND_INGAME, "cloud.module.permission")
class PermissionCommand : ICommandHandler {


    @CommandSubPath("user <user>", "Shows information about a user")
    fun on(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }
        commandSender.sendMessage("User ${permissionPlayer.getName()}:")
        commandSender.sendMessage("Highest Group: ${permissionPlayer.getHighestPermissionGroup().getName()}")
        commandSender.sendMessage("Groups: ${permissionPlayer.getAllNotExpiredPermissionGroups().map { it.getName() }}")
        val playerPermissions = permissionPlayer.getPermissions()
        if (playerPermissions.isNotEmpty())
            commandSender.sendMessage("Permissions:")
        playerPermissions.forEach {
            commandSender.sendMessage("- ${it.permissionString} : ${it.active}")
        }
    }

    private fun getPermissionPlayerByName(name: String): IPermissionPlayer? {
        val offlinePlayer =
            CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(name).awaitUninterruptibly().getNow()
                ?: return null
        return offlinePlayer.getPermissionPlayer()
    }


    @CommandSubPath("user <user> group add <group>", "Adds a group to a user lifetime")
    fun onAdd(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String
    ) {
        this.onAdd(commandSender, user, group, "lifetime")
    }


    @CommandSubPath("user <user> group add <group> <days>", "Adds a group to a user")
    fun onAdd(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String,
        @CommandArgument("days") days: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        val isInt = try {
            days.toInt()
            true
        } catch (ex: Exception) {
            false
        }
        if (days.equals("lifetime", true)) {
            permissionPlayer.addPermissionGroup(PlayerPermissionGroupInfo(group, -1))
            PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
            commandSender.sendProperty(
                "module.permission.command.perms.user.group.added.lifetime",
                permissionGroup.getName(),
                permissionPlayer.getName()
            )
            return
        }
        if (!isInt) {
            commandSender.sendProperty("module.permission.command.perms.user.day-invalid")
            return
        }
        permissionPlayer.addPermissionGroup(
            PlayerPermissionGroupInfo(
                group,
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())
            )
        )
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendProperty(
            "module.permission.command.perms.user.group.added.days",
            permissionGroup.getName(),
            days,
            permissionPlayer.getName()
        )
    }

    @CommandSubPath("user <user> group remove <group>", "Removes a group from a user")
    fun on(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.user.group-not-exist")
            return
        }
        permissionPlayer.removePermissionGroup(permissionGroup.getName())
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendProperty(
            "module.permission.command.perms.user.group.removed",
            permissionGroup.getName(),
            permissionPlayer.getName()
        )
    }

    @CommandSubPath("user <user> group set <group>", "Adds a permission to a user")
    fun handleGroupSet(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.user.group-not-exist")
            return
        }
        permissionPlayer.clearGroups()
        permissionPlayer.addPermissionGroup(PlayerPermissionGroupInfo(permissionGroup.getName(), -1))
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendProperty(
            "module.permission.command.perms.user.group.set",
            permissionGroup.getName(),
            permissionPlayer.getName()
        )
    }

    @CommandSubPath("user <user> permission add <permission> <days> <active>", "Adds a permission to a user")
    fun onPermission(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("permission") permission: String,
        @CommandArgument("days") days: String,
        @CommandArgument("active") active: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }

        val isInt = try {
            days.toInt()
            true
        } catch (ex: Exception) {
            false
        }
        if (days.equals("lifetime", true)) {
            permissionPlayer.addPermission(Permission(permission, -1, active.toBoolean()))
            PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
            commandSender.sendProperty(
                "module.permission.command.perms.user.permission.added.lifetime",
                permission,
                permissionPlayer.getName()
            )
            return
        }
        if (!isInt) {
            commandSender.sendProperty("module.permission.command.perms.user.day-invalid")
            return
        }
        commandSender.sendProperty(
            "module.permission.command.perms.user.group.added.days",
            permission,
            days,
            permissionPlayer.getName()
        )
        permissionPlayer.addPermission(
            Permission(
                permission,
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong()),
                active.toBoolean()
            )
        )
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
    }

    @CommandSubPath("user <user> permission add <permission> <days>", "Adds a permission to a user")
    fun onPermission(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("permission") permission: String,
        @CommandArgument("days") days: String
    ) {
        onPermission(commandSender, user, permission, days, true.toString())
    }

    @CommandSubPath("user <user> permission remove <permission>", "Removes a permission from a user")
    fun onPermission(
        commandSender: ICommandSender,
        @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String,
        @CommandArgument("permission") permission: String
    ) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendProperty("module.permission.command.perms.user-not-exist")
            return
        }
        if (permissionPlayer.getPermissionByName(permission) == null) {
            commandSender.sendProperty("module.permission.command.perms.user.permission.already-removed")
            return
        }
        permissionPlayer.removePermission(permission)
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendProperty("module.permission.command.perms.user.permission.removed", permission)
    }

    //groups

    fun getPermissionGroupByName(name: String) =
        PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(name)

    @CommandSubPath("groups", "Shows all permission groups")
    fun handleGroup(commandSender: ICommandSender) {
        commandSender.sendMessage("§7Permission-Groups:")
        PermissionPool.instance.getPermissionGroupManager().getAllPermissionGroups().forEach {
            commandSender.sendMessage("§8- §e" + it.getName())
        }
    }

    @CommandSubPath("group <group> create", "Creates a permission group")
    fun handleGroupCreate(commandSender: ICommandSender, @CommandArgument("group") group: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup != null) {
            commandSender.sendProperty("module.permission.command.perms.group-already-exist")
            return
        }
        PermissionGroup(group, 0).update()
        commandSender.sendProperty("module.permission.command.perms.group.created", group)
    }

    @CommandSubPath("group <group> delete", "Deletes a permission group")
    fun handleGroupDelete(commandSender: ICommandSender, @CommandArgument("group") group: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        PermissionPool.instance.getPermissionGroupManager().delete(permissionGroup)
        commandSender.sendProperty("module.permission.command.perms.group.deleted", group)
    }

    @CommandSubPath("group <group>", "Shows information about a group")
    fun handleGroup(
        commandSender: ICommandSender,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String
    ) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        commandSender.sendMessage("Group: " + permissionGroup.getName())
        permissionGroup.getPermissions().forEach {
            commandSender.sendMessage("- ${it.permissionString} : ${it.active}")
        }
    }

    @CommandSubPath("group <group> permission add <permission> <active>", "Adds a permission to a group")
    fun handleGroupPermissionAdd(
        commandSender: ICommandSender,
        @CommandArgument(
            "group",
            PermissionGroupCommandSuggestionProvider::class
        ) group: String,
        @CommandArgument("permission") permission: String,
        @CommandArgument("active") active: String
    ) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        permissionGroup.addPermission(Permission(permission, -1, active.toBoolean()))
        permissionGroup as PermissionGroup
        permissionGroup.update()
        commandSender.sendProperty(
            "module.permission.command.perms.group.permission-added",
            permission,
            permissionGroup.getName()
        )
    }

    @CommandSubPath("group <group> permission add <permission>", "Adds a permission to a group")
    fun handleGroupPermissionAdd(
        commandSender: ICommandSender,
        @CommandArgument(
            "group",
            PermissionGroupCommandSuggestionProvider::class
        ) group: String,
        @CommandArgument("permission") permission: String
    ) {
        handleGroupPermissionAdd(commandSender, group, permission, true.toString())
    }

    @CommandSubPath("group <group> permission remove <permission>", "Removes a permission from a group")
    fun handleGroupPermissionRemove(
        commandSender: ICommandSender,
        @CommandArgument(
            "group",
            PermissionGroupCommandSuggestionProvider::class
        ) group: String,
        @CommandArgument("permission") permission: String
    ) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        if (!permissionGroup.hasPermission(permission)) {
            commandSender.sendProperty("module.permission.command.perms.group.permission.already-removed")
            return
        }
        permissionGroup.removePermission(permission)
        permissionGroup as PermissionGroup
        permissionGroup.update()
        commandSender.sendProperty("module.permission.command.perms.group.permission.removed", permission)
    }

    @CommandSubPath("group <group> inheritance add <otherGroup>", "Inherits the group from the other group")
    fun handleInheritanceAdd(
        commandSender: ICommandSender,
        @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String,
        @CommandArgument(
            "otherGroup",
            PermissionGroupCommandSuggestionProvider::class
        ) otherGroup: String
    ) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        val otherPermissionGroup = getPermissionGroupByName(otherGroup)
        if (otherPermissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.other-group.not-exist")
            return
        }
        if (otherPermissionGroup == permissionGroup) {
            commandSender.sendProperty("module.permission.command.perms.inheritance.self-error")
            return
        }
        if (otherPermissionGroup.getAllInheritedPermissionGroups().contains(permissionGroup)) {
            commandSender.sendProperty("module.permission.command.perms.inheritance.recursive-error")
            return
        }
        if (permissionGroup.addInheritedPermissionGroup(otherPermissionGroup)) {
            commandSender.sendProperty(
                "module.permission.command.perms.inheritance.add.success",
                permissionGroup.getName(),
                otherPermissionGroup.getName()
            )
        } else {
            commandSender.sendProperty(
                "module.permission.command.perms.inheritance.add.failure",
                permissionGroup.getName(),
                otherPermissionGroup.getName()
            )
        }
        permissionGroup as PermissionGroup
        permissionGroup.update()
    }

    @CommandSubPath(
        "group <group> inheritance remove <otherGroup>",
        "Removes the inheritance from the group to the other group"
    )
    fun handleInheritanceRemove(
        commandSender: ICommandSender,
        @CommandArgument(
            "group",
            PermissionGroupCommandSuggestionProvider::class
        ) group: String,
        @CommandArgument(
            "otherGroup",
            PermissionGroupCommandSuggestionProvider::class
        ) otherGroup: String
    ) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.group-not-exist")
            return
        }
        val otherPermissionGroup = getPermissionGroupByName(otherGroup)
        if (otherPermissionGroup == null) {
            commandSender.sendProperty("module.permission.command.perms.other-group.not-exist")
            return
        }

        if (permissionGroup.removeInheritedPermissionGroup(otherPermissionGroup)) {
            commandSender.sendProperty(
                "module.permission.command.perms.inheritance.remove.success",
                permissionGroup.getName(),
                otherPermissionGroup.getName()
            )
        } else {
            commandSender.sendProperty(
                "module.permission.command.perms.inheritance.remove.failure",
                permissionGroup.getName(),
                otherPermissionGroup.getName()
            )
        }
        permissionGroup as PermissionGroup
        permissionGroup.update()
    }

}