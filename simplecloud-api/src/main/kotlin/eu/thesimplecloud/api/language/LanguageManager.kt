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

package eu.thesimplecloud.api.language

import com.google.common.collect.Maps
import eu.thesimplecloud.api.external.ICloudModule


open class LanguageManager : ILanguageManager {

    private val languageFiles = Maps.newConcurrentMap<ICloudModule, LoadedLanguageFile>()

    override fun registerLanguageFile(cloudModule: ICloudModule, languageFile: LoadedLanguageFile) {
        this.languageFiles[cloudModule] = languageFile
    }

    override fun getMessage(property: String, vararg placeholderValues: String): String {
        val allProperties = this.languageFiles.values.map { it.getProperties() }.flatten()
        val languageProperty = allProperties.firstOrNull { it.property == property } ?: return property
        return languageProperty.getReplacedMessage(*placeholderValues)
    }

    override fun unregisterLanguageFileByCloudModule(cloudModule: ICloudModule) {
        this.languageFiles.remove(cloudModule)
    }

    override fun clearAll() {
        this.languageFiles.clear()
    }

    override fun getAllProperties(): List<LanguageProperty> {
        return this.languageFiles.values.map { it.getProperties() }.flatten()
    }

}