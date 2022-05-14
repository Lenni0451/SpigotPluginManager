package net.lenni0451.spm.utils;

import java.lang.reflect.Field;
import java.util.Optional;

public class ReflectionUtils {

    /**
     * Get a value of a {@link Field} in a {@link Class}<br>
     * The {@link Field} is specified by the type and index in the {@link Class}
     *
     * @param clazz    The {@link Class}
     * @param instance The instance of the {@link Class} or {@code null} if static
     * @param wanted   The type of the {@link Field}
     * @param index    The index of the {@link Field}
     * @return The value of the {@link Field} or {@link Optional#empty()} if the {@link Field} doesn't exist
     */
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
