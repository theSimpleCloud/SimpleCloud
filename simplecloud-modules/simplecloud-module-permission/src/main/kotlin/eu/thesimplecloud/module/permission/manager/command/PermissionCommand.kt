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

package eu.thesimplecloud.module.permission.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.CloudPlayerCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage
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
    fun on(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
            return
        }
        commandSender.sendMessage("User ${permissionPlayer.getName()}:")
        commandSender.sendMessage("Groups: ${permissionPlayer.getAllNotExpiredPermissionGroups().map { it.getName() }}")
        commandSender.sendMessage("Permissions:")
        permissionPlayer.getPermissions().forEach {
            commandSender.sendMessage("- ${it.permissionString} : ${it.active}")
        }
    }

    private fun getPermissionPlayerByName(name: String): IPermissionPlayer? {
        val offlinePlayer = CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(name).awaitUninterruptibly().getNow()
                ?: return null
        return offlinePlayer.getPermissionPlayer(this::class.java.classLoader)
    }


    @CommandSubPath("user <user> group add <group>", "Adds a group to a user lifetime")
    fun onAdd(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String) {
        this.onAdd(commandSender, user, group, "lifetime")
    }


    @CommandSubPath("user <user> group add <group> <days>", "Adds a group to a user")
    fun onAdd(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("days") days: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
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
            commandSender.sendMessage("manager.command.perms.user.group.added.lifetime", "&aAdded group %GROUP%", permissionGroup.getName(), " lifetime to %PLAYER%", permissionPlayer.getName())
            return
        }
        if (!isInt) {
            commandSender.sendMessage("manager.command.perms.user.day-invalid", "&cThe specified day is invalid.")
            return
        }
        permissionPlayer.addPermissionGroup(PlayerPermissionGroupInfo(group, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())))
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendMessage("manager.command.perms.user.group.added.days", "&aAdded group %GROUP%", permissionGroup.getName(), " for %DAYS%", days, " days to %PLAYER%", permissionPlayer.getName())
    }

    @CommandSubPath("user <user> group remove <group>", "Removes a group from a user")
    fun on(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.user.group-not-exist", "&cGroup not found.")
            return
        }
        permissionPlayer.removePermissionGroup(permissionGroup.getName())
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendMessage("manager.command.perms.user.group.removed", "&7Group &e%GROUP%", permissionGroup.getName(), " &7removed from %PLAYER%", permissionPlayer.getName(), ".")
    }

    @CommandSubPath("user <user> group set <group>", "Adds a permission to a user")
    fun handleGroupSet(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
            return
        }
        val permissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.user.group-not-exist", "&cGroup not found.")
            return
        }
        permissionPlayer.clearGroups()
        permissionPlayer.addPermissionGroup(PlayerPermissionGroupInfo(permissionGroup.getName(), -1))
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendMessage("manager.command.perms.user.group.set", "&7Set group &e%GROUP%", permissionGroup.getName(), " &7to %PLAYER%", permissionPlayer.getName(), ".")
    }

    @CommandSubPath("user <user> permission add <permission> <days> <active>", "Adds a permission to a user")
    fun onPermission(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("permission") permission: String, @CommandArgument("days") days: String, @CommandArgument("active") active: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
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
            commandSender.sendMessage("manager.command.perms.user.permission.added.lifetime", "&aAdded permission %PERMISSION%", permission, " lifetime to %PLAYER%", permissionPlayer.getName())
            return
        }
        if (!isInt) {
            commandSender.sendMessage("manager.command.perms.user.day-invalid", "&cThe specified day is invalid.")
            return
        }
        commandSender.sendMessage("manager.command.perms.user.group.added.days", "&aAdded permission %PERMISSION%", permission, " for %DAYS%", days, " days to %PLAYER%", permissionPlayer.getName())
        permissionPlayer.addPermission(Permission(permission, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong()), active.toBoolean()))
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
    }

    @CommandSubPath("user <user> permission add <permission> <days>", "Adds a permission to a user")
    fun onPermission(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("permission") permission: String, @CommandArgument("days") days: String) {
        onPermission(commandSender, user, permission, days, true.toString())
    }

    @CommandSubPath("user <user> permission remove <permission>", "Removes a permission from a user")
    fun onPermission(commandSender: ICommandSender, @CommandArgument("user", CloudPlayerCommandSuggestionProvider::class) user: String, @CommandArgument("permission") permission: String) {
        val permissionPlayer = getPermissionPlayerByName(user)
        if (permissionPlayer == null) {
            commandSender.sendMessage("manager.command.perms.user-not-exist", "&cUser not found.")
            return
        }
        if (!permissionPlayer.hasPermission(permission)) {
            commandSender.sendMessage("manager.command.perms.user.permission.already-removed", "&cThe user doesn't have the specified permission.")
            return
        }
        permissionPlayer.removePermission(permission)
        PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        commandSender.sendMessage("manager.command.perms.user.permission.removed", "&7Permission &e%PERMISSION%", permission, " &7removed.")
    }

    //groups

    fun getPermissionGroupByName(name: String) = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(name)

    @CommandSubPath("groups", "Shows all permission groups")
    fun handleGroup(commandSender: ICommandSender) {
        commandSender.sendMessage("&7Permission-Groups:")
        PermissionPool.instance.getPermissionGroupManager().getAllPermissionGroups().forEach {
            commandSender.sendMessage("&8- &e" + it.getName())
        }
    }

    @CommandSubPath("group <group> create", "Creates a permission group")
    fun handleGroupCreate(commandSender: ICommandSender, @CommandArgument("group") group: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup != null) {
            commandSender.sendMessage("manager.command.perms.group-already-exist", "&cGroup already exist.")
            return
        }
        PermissionGroup(group).update()
        commandSender.sendMessage("manager.command.perms.group.created", "&7Group &e%GROUP%", group, " &7created.")
    }

    @CommandSubPath("group <group>", "Shows information about a group")
    fun handleGroup(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
            return
        }
        commandSender.sendMessage("Group: " + permissionGroup.getName())
        permissionGroup.getPermissions().forEach {
            commandSender.sendMessage("- ${it.permissionString} : ${it.active}")
        }
    }

    @CommandSubPath("group <group> permission add <permission> <active>", "Adds a permission to a group")
    fun handleGroupPermissionAdd(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("permission") permission: String, @CommandArgument("active") active: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
            return
        }
        permissionGroup.addPermission(Permission(permission, -1, active.toBoolean()))
        permissionGroup as PermissionGroup
        permissionGroup.update()
        commandSender.sendMessage("manager.command.perms.group.permission-added", "&7Added permission %PERMISSION%", permission, " to group %GROUP%", permissionGroup.getName(), ".")
    }

    @CommandSubPath("group <group> permission add <permission>", "Adds a permission to a group")
    fun handleGroupPermissionAdd(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("permission") permission: String) {
        handleGroupPermissionAdd(commandSender, group, permission, true.toString())
    }

    @CommandSubPath("group <group> permission remove <permission>", "Removes a permission from a group")
    fun handleGroupPermissionRemove(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("permission") permission: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
            return
        }
        if (!permissionGroup.hasPermission(permission)) {
            commandSender.sendMessage("manager.command.perms.group.permission.already-removed", "&cThe group doesn't have the specified permission.")
            return
        }
        permissionGroup.removePermission(permission)
        permissionGroup as PermissionGroup
        permissionGroup.update()
        commandSender.sendMessage("manager.command.perms.group.permission.removed", "&7Permission &e%PERMISSION%", permission, " &7removed.")
    }

    @CommandSubPath("group <group> inheritance add <otherGroup>", "Inherits the group from the other group")
    fun handleInheritanceAdd(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("otherGroup", PermissionGroupCommandSuggestionProvider::class) otherGroup: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
            return
        }
        val otherPermissionGroup = getPermissionGroupByName(otherGroup)
        if (otherPermissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.other-group.not-exist", "&cOther group not found.")
            return
        }
        if (otherPermissionGroup == permissionGroup) {
            commandSender.sendMessage("manager.command.perms.inheritance.self-error", "&cThe group cannot inherit from itself.")
            return
        }
        if (otherPermissionGroup.getAllInheritedPermissionGroups().contains(permissionGroup)) {
            commandSender.sendMessage("manager.command.perms.inheritance.recursive-error", "&cThe other group is already inheriting the group.")
            return
        }
        if (permissionGroup.addInheritedPermissionGroup(otherPermissionGroup)) {
            commandSender.sendMessage("manager.command.perms.inheritance.add.success", "&7Group &e%GROUP%", permissionGroup.getName(), " &7is now inheriting from &e%OTHER_GROUP%", otherPermissionGroup.getName(), "&7.")
        } else {
            commandSender.sendMessage("manager.command.perms.inheritance.add.failure", "&cGroup %GROUP%", permissionGroup.getName(), " is already inheriting from %OTHER_GROUP%", otherPermissionGroup.getName(), ".")
        }
        permissionGroup as PermissionGroup
        permissionGroup.update()
    }

    @CommandSubPath("group <group> inheritance remove <otherGroup>", "Removes the inheritance from the group to the other group")
    fun handleInheritanceRemove(commandSender: ICommandSender, @CommandArgument("group", PermissionGroupCommandSuggestionProvider::class) group: String, @CommandArgument("otherGroup", PermissionGroupCommandSuggestionProvider::class) otherGroup: String) {
        val permissionGroup = getPermissionGroupByName(group)
        if (permissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.group-not-exist", "&cGroup not found.")
            return
        }
        val otherPermissionGroup = getPermissionGroupByName(otherGroup)
        if (otherPermissionGroup == null) {
            commandSender.sendMessage("manager.command.perms.other-group.not-exist", "&cOther group not found.")
            return
        }

        if (permissionGroup.removeInheritedPermissionGroup(otherPermissionGroup)) {
            commandSender.sendMessage("manager.command.perms.inheritance.remove.success", "&7Group &e%GROUP%", permissionGroup.getName(), " &7is no longer inheriting from &e%OTHER_GROUP%", otherPermissionGroup.getName(), "&7.")
        } else {
            commandSender.sendMessage("manager.command.perms.inheritance.remove.failure", "&cGroup &e%GROUP%", permissionGroup.getName(), " &7is not inheriting from &e%OTHER_GROUP%", otherPermissionGroup.getName(), "&7.")
        }
        permissionGroup as PermissionGroup
        permissionGroup.update()
    }

}