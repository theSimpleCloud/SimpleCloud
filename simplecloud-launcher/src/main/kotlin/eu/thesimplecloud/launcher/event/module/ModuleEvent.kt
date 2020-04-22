package eu.thesimplecloud.launcher.event.module

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.launcher.external.module.LoadedModule

open class ModuleEvent(val module: LoadedModule) : IEvent