package net.Lenni0451.spm.utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ThreadUtils {

    /**
     * Get a set of all running threads
     */
    public static Set<Thread> getALlThreads() {
        return Thread.getAllStackTraces().keySet();
    }

    /**
     * Check if a thread has been started from a class loaded by a given classloader
     *
     * @param thread      The thread to check
     * @param classLoader The classloader of the starter class
     */
    public static boolean isThreadFromClassLoader(final Thread thread, final ClassLoader classLoader) {
        if (classLoader.equals(thread.getClass().getClassLoader())) return true;
        Optional<Runnable> runnable = ReflectionUtils.getField(Thread.class, thread, Runnable.class, 0);
        return runnable.isPresent() && classLoader.equals(runnable.get().getClass().getClassLoader());
    }

    /**
     * Get all threads started by a class loaded from the given classloader
     *
     * @param classLoader The classloader of the starter class
     */
    public static Set<Thread> getAllThreadsFromClassLoader(final ClassLoader classLoader) {
        Set<Thread> threads = new HashSet<>();
        for (Thread thread : getALlThreads()) {
            if (isThreadFromClassLoader(thread, classLoader)) threads.add(thread);
        }
        return threads;
    }

}
