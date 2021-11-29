package net.lenni0451.spm.utils;

import java.io.File;

public class FileUtils {

    public static File[] listFiles(final File f) {
        File[] files = f.listFiles();
        if (files == null) return new File[0];
        return files;
    }

    public static void delete(final File file) {
        if (file.isDirectory()) for (File f : listFiles(file)) delete(f);
        file.delete();
    }

}
