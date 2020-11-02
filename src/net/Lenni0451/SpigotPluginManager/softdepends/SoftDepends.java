package net.Lenni0451.SpigotPluginManager.softdepends;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public enum SoftDepends {

    MVdWUpdater;

    public boolean isInstalled() {
        try {
            PluginManager.getInstance().getPluginUtils().getPlugin(this.name());
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

}
