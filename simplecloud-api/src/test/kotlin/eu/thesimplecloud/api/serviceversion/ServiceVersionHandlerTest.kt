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

package eu.thesimplecloud.api.serviceversion

import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.service.version.ServiceVersionHandler
import eu.thesimplecloud.api.service.version.type.ServiceAPIType
import eu.thesimplecloud.api.service.version.type.ServiceVersionType
import eu.thesimplecloud.jsonlib.JsonLib
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 14.06.2020
 * Time: 18:47
 * @author Frederick Baier
 */
class ServiceVersionHandlerTest {

    val testContent = "[\n" +
            "  {\n" +
            "    \"name\": \"BUNGEECORD\",\n" +
            "    \"serviceAPIType\": \"BUNGEECORD\",\n" +
            "    \"downloadURL\": \"https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"TRAVERTINE\",\n" +
            "    \"serviceAPIType\": \"BUNGEECORD\",\n" +
            "    \"downloadURL\": \"https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"WATERFALL\",\n" +
            "    \"serviceAPIType\": \"BUNGEECORD\",\n" +
            "    \"downloadURL\": \"https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"HEXACORD\",\n" +
            "    \"serviceAPIType\": \"BUNGEECORD\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/hexacord/HexaCord-v246.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"VELOCITY\",\n" +
            "    \"serviceAPIType\": \"VELOCITY\",\n" +
            "    \"downloadURL\": \"https://ci.velocitypowered.com/job/velocity-1.1.0/lastSuccessfulBuild/artifact/proxy/build/libs/velocity-proxy-1.1.0-SNAPSHOT-all.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_7_10\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_8_8\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_9_4\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_10_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_11_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_12_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_13_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_14_4\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"SPIGOT_1_15_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_7_10\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/PaperSpigot-1.7.10-R0.1-SNAPSHOT-latest.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_8_8\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_11_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/PaperSpigot-1.11.2-b1104.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_12_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/Paper-1.12.2-b1618.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_13_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/Paper-1.13.2-b655.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_14_4\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/Paper-1.14.4-b243.jar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"PAPER_1_15_2\",\n" +
            "    \"serviceAPIType\": \"SPIGOT\",\n" +
            "    \"downloadURL\": \"https://yivesmirror.com/files/paper/Paper-1.15.2-b350.jar\"\n" +
            "  }\n" +
            "]"

    @Test
    fun versions_test() {
        val list = JsonLib.fromJsonString(testContent).getObject(Array<ServiceVersion>::class.java).toList()
        val serviceVersionHandler = ServiceVersionHandler(list)
        assertEquals(4, serviceVersionHandler.getVersions(ServiceAPIType.BUNGEECORD).size)
        assertEquals(1, serviceVersionHandler.getVersions(ServiceAPIType.VELOCITY).size)
        assertEquals(16, serviceVersionHandler.getVersions(ServiceAPIType.SPIGOT).size)
    }

    @Test
    fun prefix_test() {
        val list = JsonLib.fromJsonString(testContent).getObject(Array<ServiceVersion>::class.java).toList()
        val serviceVersionHandler = ServiceVersionHandler(list)
        assertEquals(1, serviceVersionHandler.getVersionsByPrefix("BUNGEECORD").size)
        assertEquals(7, serviceVersionHandler.getVersionsByPrefix("PAPER").size)
        assertEquals(9, serviceVersionHandler.getVersionsByPrefix("SPIGOT").size)
    }

    @Test
    fun suffix_test() {
        val list = JsonLib.fromJsonString(testContent).getObject(Array<ServiceVersion>::class.java).toList()
        val serviceVersionHandler = ServiceVersionHandler(list)
        assertEquals(
            mutableListOf("1_7_10", "1_8_8", "1_11_2", "1_12_2", "1_13_2", "1_14_4", "1_15_2"),
            serviceVersionHandler.getAllVersionSuffixes("PAPER")
        )
        assertEquals(7, serviceVersionHandler.getVersionsByPrefix("PAPER").size)
        assertEquals(9, serviceVersionHandler.getVersionsByPrefix("SPIGOT").size)
    }

    @Test
    fun prefix_by_service_version_test() {
        val list = JsonLib.fromJsonString(testContent).getObject(Array<ServiceVersion>::class.java).toList()
        val serviceVersionHandler = ServiceVersionHandler(list)
        assertEquals(
            mutableSetOf("SPIGOT", "PAPER"),
            serviceVersionHandler.getPrefixesByServiceVersionType(ServiceVersionType.SERVER)
        )
    }

}