package eu.thesimplecloud.launcher.event.command

import eu.thesimplecloud.launcher.console.command.CommandData

class CommandUnregisteredEvent(command: CommandData) : CommandEvent(command)