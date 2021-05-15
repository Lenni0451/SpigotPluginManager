package net.lenni0451.spm.utils;

public class PluginInfo {

    private final String name;
    private final int id;
    private final String installedVersion;
    private final String fileName;

    public PluginInfo(final String name, final int id, final String installedVersion, final String fileName) {
        this.name = name;
        this.id = id;
        this.installedVersion = installedVersion;
        this.fileName = fileName;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public String getInstalledVersion() {
        return this.installedVersion;
    }

    public String getFileName() {
        return this.fileName;
    }

}
