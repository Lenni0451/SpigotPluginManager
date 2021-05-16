package net.lenni0451.spm.utils;

import java.io.File;

public class FileUtils {

    public static File[] listFiles(final File f) {
        File[] files = f.listFiles();
        if (files == null) return new File[0];
        return files;
    }

}
