package net.Lenni0451.spm.utils;

import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtils {

    public static <T> Optional<T> getField(final Class<?> clazz, final Object instance, final Class<? extends T> wanted, final int index) {
        int i = 0;
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.getType().equals(wanted)) continue;
                field.setAccessible(true);
                if (i == index) return Optional.of((T) field.get(instance));
                i++;
            }
        } catch (Throwable ignored) {
        }
        return Optional.empty();
    }

}
