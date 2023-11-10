package net.lenni0451.pluginmanager.utils;

import net.lenni0451.reflect.stream.RStream;

import java.util.Set;
import java.util.stream.Collectors;

public class ThreadUtils {

    public static boolean isFromClassLoader(final Thread thread, final ClassLoader classLoader) {
        if (classLoader.equals(thread.getClass().getClassLoader())) return true;
        return RStream
                .of(thread)
                .withSuper()
                .fields()
                .filter(Runnable.class)
                .opt(0)
                .map(f -> classLoader.equals(f.get().getClass().getClassLoader()))
                .orElse(false);
    }

    public static Set<Thread> getAllThreadsFrom(final ClassLoader classLoader) {
        return Thread
                .getAllStackTraces()
                .keySet()
                .stream()
                .filter(t -> isFromClassLoader(t, classLoader))
                .collect(Collectors.toSet());
    }

}
