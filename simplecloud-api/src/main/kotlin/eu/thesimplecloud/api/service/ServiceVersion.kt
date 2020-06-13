/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.api.service

enum class ServiceVersion(val serviceVersionType: ServiceVersionType, val downloadLink: String) {

    //Proxy Versions default minecraft

    BUNGEECORD(ServiceVersionType.BUNGEE_DEFAULT, "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar"),
    TRAVERTINE(ServiceVersionType.BUNGEE_DEFAULT, "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar"),
    WATERFALL(ServiceVersionType.BUNGEE_DEFAULT, "https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar"),
    HEXACORD(ServiceVersionType.BUNGEE_DEFAULT, "https://yivesmirror.com/files/hexacord/HexaCord-v246.jar"),

    VELOCITY(ServiceVersionType.VELOCITY_DEFAULT, "https://ci.velocitypowered.com/job/velocity-1.1.0/lastSuccessfulBuild/artifact/proxy/build/libs/velocity-proxy-1.1.0-SNAPSHOT-all.jar"),

    SPIGOT_1_7_10(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar"),
    SPIGOT_1_8_8(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar"),
    SPIGOT_1_9_4(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar"),
    SPIGOT_1_10_2(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar"),
    SPIGOT_1_11_2(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar"),
    SPIGOT_1_12_2(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar"),
    SPIGOT_1_13_2(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar"),
    SPIGOT_1_14_4(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar"),
    SPIGOT_1_15_2(ServiceVersionType.SERVER_DEFAULT, "https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar"),

    PAPER_1_7_10(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/PaperSpigot-1.7.10-R0.1-SNAPSHOT-latest.jar"),
    PAPER_1_8_8(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar"),
    PAPER_1_11_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/PaperSpigot-1.11.2-b1104.jar"),
    PAPER_1_12_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.12.2-b1618.jar"),
    PAPER_1_13_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.13.2-b655.jar"),
    PAPER_1_14_4(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.14.4-b243.jar"),
    PAPER_1_15_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.15.2-b350.jar");

    //NUKKIT(ServiceVersionType.SERVER_DEFAULT, "https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/lastSuccessfulBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar");




    enum class ServiceVersionType() {
        BUNGEE_DEFAULT,
        VELOCITY_DEFAULT,
        SERVER_DEFAULT,

        PROXY_POCKET_EDITION,
        SERVER_POCKET_EDITION
    }

}