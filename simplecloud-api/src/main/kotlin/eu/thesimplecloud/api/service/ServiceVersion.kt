package eu.thesimplecloud.api.service

enum class ServiceVersion(val serviceVersionType: ServiceVersionType, val downloadLink: String) {

    //Proxy Versions default minecraft

    BUNGEECORD(ServiceVersionType.BUNGEE_DEFAULT, "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar"),
    TRAVERTINE(ServiceVersionType.BUNGEE_DEFAULT, "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar"),
    WATERFALL(ServiceVersionType.BUNGEE_DEFAULT, "https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar"),
    HEXACORD(ServiceVersionType.BUNGEE_DEFAULT, "https://yivesmirror.com/files/hexacord/HexaCord-v246.jar"),

    VELOCITY(ServiceVersionType.VELOCITY_DEFAULT, "https://ci.velocitypowered.com/job/velocity/lastSuccessfulBuild/artifact/proxy/build/libs/velocity-proxy-1.0.8-SNAPSHOT-all.jar"),

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
    PAPER_1_13_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.13.2-b624.jar"),
    PAPER_1_14_4(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.14.4-b210.jar"),
    PAPER_1_15_2(ServiceVersionType.SERVER_DEFAULT, "https://yivesmirror.com/files/paper/Paper-1.15.2-b143.jar");

    //NUKKIT(ServiceVersionType.SERVER_DEFAULT, "https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/lastSuccessfulBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar");




    enum class ServiceVersionType() {
        BUNGEE_DEFAULT,
        VELOCITY_DEFAULT,
        SERVER_DEFAULT,

        PROXY_POCKET_EDITION,
        SERVER_POCKET_EDITION
    }

}