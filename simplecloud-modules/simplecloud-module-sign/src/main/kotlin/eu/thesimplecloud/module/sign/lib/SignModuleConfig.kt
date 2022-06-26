/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.sign.lib

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.module.sign.lib.group.GroupLayoutsContainer
import eu.thesimplecloud.module.sign.lib.layout.LayoutType
import eu.thesimplecloud.module.sign.lib.layout.SignLayout
import eu.thesimplecloud.module.sign.lib.layout.SignLayoutContainer
import eu.thesimplecloud.module.sign.lib.sign.CloudSignContainer

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 17:01
 * @author Frederick Baier
 */
class SignModuleConfig(
    val signLayoutContainer: SignLayoutContainer,
    val signContainer: CloudSignContainer,
    val groupsLayoutContainer: GroupLayoutsContainer
) {

    fun getSignLayoutForGroup(layoutType: LayoutType, groupName: String): SignLayout {
        val groupLayouts = this.groupsLayoutContainer.getGroupLayoutsByGroupName(groupName)
            ?: return getDefaultLayout(layoutType)
        val layoutName = groupLayouts.getLayoutNameByType(layoutType)
        return this.signLayoutContainer.getLayoutByName(layoutType, layoutName)!!
    }

    private fun getDefaultLayout(layoutType: LayoutType): SignLayout {
        return this.signLayoutContainer.getDefaultLayout(layoutType)
    }

    fun update() {
        property = CloudAPI.instance.getGlobalPropertyHolder().setProperty("sign-config", this)
    }

    companion object {

        @Volatile
        private var property: IProperty<SignModuleConfig>? = null

        fun getConfig(): SignModuleConfig {
            if (this.property == null) {
                this.property =
                    CloudAPI.instance.getGlobalPropertyHolder().requestProperty<SignModuleConfig>("sign-config")
                        .getBlocking()
            }
            return this.property!!.getValue()
        }

    }

}