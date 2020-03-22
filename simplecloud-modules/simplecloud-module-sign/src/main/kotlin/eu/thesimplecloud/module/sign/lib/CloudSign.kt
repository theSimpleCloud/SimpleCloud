package eu.thesimplecloud.module.sign.lib

import eu.thesimplecloud.api.location.TemplateLocation

data class CloudSign(
        val templateLocation: TemplateLocation,
        val forGroup: String
)