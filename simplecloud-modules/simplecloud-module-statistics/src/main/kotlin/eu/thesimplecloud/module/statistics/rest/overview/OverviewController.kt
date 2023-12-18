package eu.thesimplecloud.module.statistics.rest.overview

import eu.thesimplecloud.module.rest.annotation.RequestMapping
import eu.thesimplecloud.module.rest.annotation.RequestType
import eu.thesimplecloud.module.rest.annotation.RestController
import eu.thesimplecloud.module.rest.controller.IController
import eu.thesimplecloud.module.statistics.rest.overview.data.Overview

@RestController(path = "overview/")
class OverviewController : IController {

    @RequestMapping(RequestType.GET, additionalPath = "", permission = "web.cloud.overview.get")
    fun handleOverviewRequest() : Overview
    {
        return Overview.create()
    }


}