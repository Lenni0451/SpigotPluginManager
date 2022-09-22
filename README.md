# SpigotPluginManager
A fresh and easy to use plugin manager for spigot.\
PluginManager (Yes, a really creative name) aims to be the perfect replacement for every other plugin manager you ever had. It has many already seen featured and also new ones like an easy to use gui.

Not convinced yet? Download it and try it out. Your time is worth it!

## Contribute
If you want to contribute keep the added code in the same style as the existing one.

## SpigotMC
Check out my [SpigotMC Thread](https://www.spigotmc.org/resources/pluginmanager.69061/) of this plugin and leave a rating if you think my time is worth it.

## Downloading
You can download the latest version through the [GitHub releases](https://github.com/Lenni0451/SpigotPluginManager/releases) page.\
If you want to download the latest (maybe unstable build) refer to my [Jenkins server](https://build.lenni0451.net/job/SpigotPluginManager/).

To use SpigotPluginManager as a dependency when developing your own plugin you can use my [maven server](https://maven.lenni0451.net/#/releases/net/lenni0451/SpigotPluginManager).\
To add the repository in gradle add this to your ``repositories`` block:
````` groovy
maven {
    //Or use '/snapshots' if you want the latest dev build
    url "https://maven.lenni0451.net/releases" 
}
`````
and this to your ``dependencies`` block:
````` groovy
//You should maybe update this version. This is only an example
implementation "net.lenni0451:SpigotPluginManager:3.7.2"
`````

## Building
First of all make sure you set ``updatable`` to ``false`` in the ``gradle.properties`` file if it is not already. This will prevent SpigotPluginManager from updating itself on startup.\
To build SpigotPluginManager yourself you just need to run ``gradlew build`` in the project root.\
You can find the compiled jar in the ``build/libs/`` folder.
