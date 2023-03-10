package net.lenni0451.pluginmanager.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlFormatter {

    public static Map<String, Object> buildYamlStyle(final Map<String, String> map) {
        Map<String, Object> formatted = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            Map<String, Object> currentMap = formatted;
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (i == parts.length - 1) {
                    currentMap.put(part, entry.getValue());
                } else {
                    Map<String, Object> nextMap = (Map<String, Object>) currentMap.get(part);
                    if (nextMap == null) {
                        nextMap = new LinkedHashMap<>();
                        currentMap.put(part, nextMap);
                    }
                    currentMap = nextMap;
                }
            }
        }
        return formatted;
    }

    public static String format(final Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        format(map, builder, 0);
        return builder.toString();
    }

    private static void format(final Map<String, Object> map, final StringBuilder builder, final int indent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            for (int i = 0; i < indent; i++) builder.append("  ");
            builder.append(entry.getKey()).append(": ");
            if (entry.getValue() instanceof Map) {
                builder.append("\n");
                format((Map<String, Object>) entry.getValue(), builder, indent + 1);
            } else {
                builder.append(entry.getValue()).append("\n");
            }
        }
    }

}
