package eu.thesimplecloud.launcher.event.module

import eu.thesimplecloud.launcher.external.module.LoadedModule

class ModuleUnloadedEvent(module: LoadedModule) : ModuleEvent(module) {
}