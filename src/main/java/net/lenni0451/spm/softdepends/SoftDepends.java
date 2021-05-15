package net.lenni0451.spm.softdepends;

import net.lenni0451.spm.PluginManager;

public enum SoftDepends {

    MVdWUpdater;

    public boolean isInstalled() {
        return PluginManager.getInstance().getPluginUtils().isPluginLoaded(this.name());
    }

}
