package eu.thesimplecloud.base.manager.groups

import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

interface ICloudServiceGroupFileHandler {

    /**
     * Saved the specified [ICloudServiceGroup] to a file
     */
    fun saveGroup(cloudServiceGroup: ICloudServiceGroup)

    /**
     * Deletes the file found by the specified [ICloudServiceGroup]
     */
    fun deleteGroup(cloudServiceGroup: ICloudServiceGroup)

    /**
     * Loads all groups from the files.
     */
    fun loadGroups(): Set<ICloudServiceGroup>

}