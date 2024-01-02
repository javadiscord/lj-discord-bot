package com.javadiscord.bot.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Executor {
    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void execute(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

    public static void run(Runnable runnable, int period, TimeUnit timeUnit) {
        EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, 0, period, timeUnit);
    }
}
