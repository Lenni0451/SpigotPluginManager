package net.lenni0451.spm.utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ThreadUtils {

    /**
     * Get a {@link Set} of all running {@link Thread}s
     *
     * @return The {@link Set}
     */
    public static Set<Thread> getALlThreads() {
        return Thread.getAllStackTraces().keySet();
    }

    /**
     * Check if the given {@link Thread} is started by a {@link Class} loaded from the given {@link ClassLoader}
     *
     * @param thread      The {@link Thread} to check
     * @param classLoader The {@link ClassLoader} of the {@link Class} to check
     * @return {@code true} if the {@link Thread} is started by a {@link Class} loaded from the given {@link ClassLoader}
     */
    public static boolean isThreadFromClassLoader(final Thread thread, final ClassLoader classLoader) {
        if (classLoader.equals(thread.getClass().getClassLoader())) return true;
        Optional<Runnable> runnable = ReflectionUtils.getField(Thread.class, thread, Runnable.class, 0);
        return runnable.isPresent() && classLoader.equals(runnable.get().getClass().getClassLoader());
    }

    /**
     * Get a {@link Set} of all {@link Thread}s started by a {@link Class} loaded from the given {@link ClassLoader}
     *
     * @param classLoader The {@link ClassLoader} of the {@link Class} to check
     * @return The {@link Set}
     */
    public static Set<Thread> getAllThreadsFromClassLoader(final ClassLoader classLoader) {
        Set<Thread> threads = new HashSet<>();
        for (Thread thread : getALlThreads()) {
            if (isThreadFromClassLoader(thread, classLoader)) threads.add(thread);
        }
        return threads;
    }

}
