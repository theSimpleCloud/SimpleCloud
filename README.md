<p align="center">
  <img src="https://i.imgur.com/eTQJ1IX.png" alt="Logo">
</p>

<p>
  <p align="center">
    A simple alternative to other minecraft cloud systems
    <br />
    <a href="https://www.spigotmc.org/resources/simplecloud-simplify-your-network.79466/o">SpigotMC</a>
    ·
    <a href="https://repo.thesimplecloud.eu/ui/repos/tree/General/artifactory-build-info">Repository</a>
    ·
    <a href="http://dashboard-nossl.thesimplecloud.eu">Dashboard</a>
    ·
    <a href="https://ci.thesimplecloud.eu/job/SimpleCloudOrganization/job/SimpleCloud/">Jenkins</a>
    ·
    <a href="https://discord.gg/EzGVHXG3GE">Discord</a>
    ·
    <a href="https://ts3server://thesimplecloud.eu">Teamspeak</a>
  </p>

</p>

<br />
<br />

<details open="open">
  <summary>Overview</summary>
  <ol>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#requirements">Requirements</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#features">Features</a></li>
    <li>
      <a href="#modules">Modules</a>
      <ul>
        <li><a href="#internal-wrapper">Internal-Wrapper Module</a></li>
        <li><a href="#signs">Sign Module</a></li>
        <li><a href="#permissions">Permission Module</a></li>
        <li><a href="#proxy">Proxy Module</a></li>
        <li><a href="#hub">Hub Module</a></li>
        <li><a href="#notify">Notify Module</a></li>
        <li><a href="#rest">REST Module</a></li>
        <li><a href="#statistics">Statistics Module</a></li>
        <li><a href="#chat + tab">Chat + Tab Module</a></li>
      </ul>
    </li>
    <li><a href="#dashboard">Dashboard</a></li>
  </ol>
</details>

<br />

## Getting Started

### Requirements
* **Java 8** or higher
* **[MongoDB](https://www.mongodb.com/cloud/atlas/lp/try2-de?utm_source=google&utm_campaign=gs_emea_germany_search_core_brand_atlas_desktop&utm_term=mongodb&utm_medium=cpc_paid_search&utm_ad=e&utm_ad_campaign_id=12212624524&gclid=Cj0KCQjw5auGBhDEARIsAFyNm9EkpiB2K-5CMNxHkHcY7VbdNE_4HrbwDOSrMmjgNAve270Hnd9pjRoaAqFgEALw_wcB)** or **[SQL](https://go.mariadb.com/download-mariadb-server-community.html?utm_source=google&utm_medium=ppc&utm_campaign=MKG-Search-Google-Branded-EMEA-bd-Server-DL&gclid=Cj0KCQjw5auGBhDEARIsAFyNm9HBSH7xv8vFObvU9Xk8-bgYskrjfU53aBSkyehaGOxQQx2veRbC6-caAtJyEALw_wcB)** database
* min. **2GB** Memory and **2 virtual cores**

### Installation
<ol>
  <li>Download the cloud on SpigotMC</li>
  <li>Unzip the folder and execute the start file</li>
  <li>Follow the setup instructions</li>
  <li>Connect a wrapper to your manager. It is recommended to use the InternalWrapperModule for this. You can find it below.</li>
  <li>Create your first group by typing create into the console</li>
  <li>For more information type help</li>
  <li>Have fun :D</li>
</ol>

<br />

## Features
* Dashboard
* Language-System
* Multi-Root
* Multi-Proxy
* Module-System
* **MongoDB** and **SQL** support
* Powerful **API**
* Powerful **REST-API**
* Support for **Spigot**, **BungeeCord** and **Velocity**
* Template-System

<br />

## Dashboard
SimpleCloud provides a **Dashboard accessible** for every user.
The domain for the **Dashboard** is: **http://dashboard-nossl.thesimplecloud.eu**.
Now you have to enter the **IP-Address** of your server follwed by the port of the **REST-Module.**
<br />
<br />
The default port of the **REST-Module** ist **8585**. So an ip would be **55.55.55.55:8585**. <br />
The username and password can be found in **"modules/rest/users.json"**

<br />

## Modules
SimpleCloud provides some modules by default.

### Internal-Wrapper
The **Internal-Wrapper Module** starts a wrapper every time the manager of the cloud gets started.
This wrapper does only run when the manager is running. It connects **automatically** with no need to set it up.
The wrapper will be automatically named **"InternalWrapper"** and it will have **2GB of memory**.
<br />

To edit the memory of the wrapper you can use the command: <br />
``edit wrapper InternalWrapper maxMemory <amount in MB>``

<br />

### Signs
The **Sign Module** is used to show available services for players in the lobby via a **sign**.
The players can click on that sign and will be sent to the server. The layout of the signs is **fully customizable**.

![SignModule](https://i.imgur.com/w534aZG.gif "SignModule")

<br />

### Permissions
With the **Permission Module**, you can easily manage your permissions.
You can define groups and add permissions to them. You can also add permissions to single users.

![PermissionModule](https://i.imgur.com/5LXMwCk.jpg "PermissionModule")

<br />

### Proxy
The **Proxy Module** is used to manage your proxies.
It controls the **MOTD**, **Tablist**, **maintenance** and the **online count**.
The online count of proxies of one group will be summed up and displayed in the player info.

![ProxyModuleTab](https://i.imgur.com/2djSS9l.jpg "ProxyModuleTab")

![ProxyModuleMotd](https://i.imgur.com/dkuxYM7.png "ProxyModuleMotd")

![ProxyModuleMotdMaintenance](https://i.imgur.com/eCSXSJo.png "ProxyModuleMotdMaintenance")

<br />

**Permissions:**
<br />
````
Maintenance join: cloud.maintenance.join
Full join: cloud.full.join
````

<br />

### Hub
The **Hub Module** provides a hub command for players to switch to a **lobby server**.

<br />

### Notify
The **Notify Module** sends notifications to players that have the required permission when a server gets **started** or **stopped**.

![NotifyModule](https://i.imgur.com/7lcjXbN.jpg "NotifyModule")

<br />

**Permissions:**
<br />
````
cloud.module.notify.messages
````

<br />

### REST
The **REST Module** provides a **restful API**. It is necessary for the **dashboard**.

<br />

### Statistics
The **Statistics Module** saves some statistics of the cloud. The data is displayed on the **dashboard**.

<br />

### Chat + Tab
The **Chat + Tab Module** provides **prefixes** and **suffixes** in the tablist and adds a **chat configuration**.
