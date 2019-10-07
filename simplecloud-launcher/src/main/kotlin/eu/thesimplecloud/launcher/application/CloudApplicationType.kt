package eu.thesimplecloud.launcher.application

enum class CloudApplicationType {

    MANAGER, WRAPPER;

    fun getApplicationName() = this.name.substring(0, 1) + this.name.substring(1).toLowerCase()


}