package net.lenni0451.spm.utils;

import java.io.File;

public class FileUtils {

    /**
     * List all {@link File}s in a directory
     *
     * @param f The directory
     * @return The list of {@link File}s
     */
    public static File[] listFiles(final File f) {
        File[] files = f.listFiles();
        if (files == null) return new File[0];
        return files;
    }

    /**
     * Delete a {@link File} and all of its children
     *
     * @param file The {@link File} to delete
     */
    public static void delete(final File file) {
        if (file.isDirectory()) {
            for (File f : listFiles(file)) delete(f);
        }
        file.delete();
    }

}
