package net.Lenni0451.SpigotPluginManager.softdepends;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public enum SoftDepends {

    MVdWUpdater;

    public boolean isInstalled() {
        return PluginManager.getInstance().getPluginUtils().isPluginLoaded(this.name());
    }

}
