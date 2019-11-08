package eu.thesimplecloud.base.wrapper.process

class CloudServiceProcessManager : ICloudServiceProcessManager {

    private val registeredProcesses = HashSet<ICloudServiceProcess>()

    override fun registerServiceProcess(cloudServiceProcess: ICloudServiceProcess) {
        this.registeredProcesses.add(cloudServiceProcess)
    }

    override fun unregisterServiceProcess(cloudServiceProcess: ICloudServiceProcess) {
        this.registeredProcesses.remove(cloudServiceProcess)
    }

    override fun getAllProcesses(): Set<ICloudServiceProcess> = this.registeredProcesses



}