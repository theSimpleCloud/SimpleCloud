package eu.thesimplecloud.launcher.event.command

import eu.thesimplecloud.launcher.console.command.CommandData

class CommandRegisteredEvent(command: CommandData) : CommandEvent(command)