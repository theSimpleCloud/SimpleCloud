package eu.thesimplecloud.module.rest.defaultcontroller.dump

import eu.thesimplecloud.base.manager.dump.DumpJsonFile
import eu.thesimplecloud.module.rest.annotation.RequestMapping
import eu.thesimplecloud.module.rest.annotation.RequestType
import eu.thesimplecloud.module.rest.annotation.RestController
import eu.thesimplecloud.module.rest.controller.IController

/**
 * Created by MrManHD
 * Class create at 14.11.23 16:46
 */

@RestController("cloud/dump/")
class DumpController : IController {

    @RequestMapping(RequestType.GET, "", "web.cloud.dump.get")
    fun handleGetAllTemplates(): DumpJsonFile {
        return DumpJsonFile.createJsonFile()
    }

}