package net.lenni0451.spm.utils;

import org.bukkit.Material;

import java.lang.reflect.Field;

public class VersionMaterial {

    /**
     * Get a {@link Material} from a version specific name<br>
     *
     * @param names The possible names of the {@link Material}
     * @return The {@link Material} or {@code null} if not found
     */
    public static Material getMaterial(final String... names) {
        if (names.length == 0) throw new IllegalArgumentException("You need to pass at least one material name");

        for (String name : names) {
            try {
                Field f = Material.class.getDeclaredField(name);
                f.setAccessible(true);
                return (Material) f.get(null);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

}
