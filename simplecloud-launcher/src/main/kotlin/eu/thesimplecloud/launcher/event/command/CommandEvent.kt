package eu.thesimplecloud.launcher.event.command

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.launcher.console.command.CommandData

open class CommandEvent(val command: CommandData) : IEvent