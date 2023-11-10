package net.lenni0451.pluginmanager.i18n;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TranslationsSerializer {

    public static void serialize(final BiConsumer<String, String> consumer) throws IllegalAccessException {
        Map<String, Field> paths = buildPaths(I18n.class, "");
        for (Map.Entry<String, Field> entry : paths.entrySet()) consumer.accept(entry.getKey(), (String) entry.getValue().get(null));
    }

    public static void deserialize(final Function<String, String> function) throws IllegalAccessException {
        Map<String, Field> paths = buildPaths(I18n.class, "");
        for (Map.Entry<String, Field> entry : paths.entrySet()) {
            String value = function.apply(entry.getKey());
            if (value != null) entry.getValue().set(null, value);
        }
    }

    private static Map<String, Field> buildPaths(final Class<?> current, final String path) {
        Map<String, Field> paths = new LinkedHashMap<>();
        for (Field field : current.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().equals(String.class)) continue;

            paths.put(path + field.getName(), field);
        }
        for (Class<?> clazz : current.getDeclaredClasses()) paths.putAll(buildPaths(clazz, path + clazz.getSimpleName() + "."));
        return paths;
    }

}
