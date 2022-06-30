package de.eldritch.turtle.server.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class MiscUtil {
    public static boolean isOneNull(Object... objects) {
        if (objects == null) return false;

        for (Object object : objects) {
            if (object == null)
                return true;
        }
        return false;
    }

    @SuppressWarnings("BusyWait")
    public static void await(@NotNull BooleanSupplier condition, int timeout, @NotNull TimeUnit unit) throws TimeoutException, InterruptedException {
        final long timeoutMillis = System.currentTimeMillis() + unit.toMillis(timeout);

        while (!condition.getAsBoolean()) {
            if (timeoutMillis >= System.currentTimeMillis())
                throw new TimeoutException("Timed out");
            Thread.sleep(20L);
        }
    }

    public static @NotNull String cut(@NotNull String str, int length) {
        if (str.length() > length)
            return str.substring(length);
        return str;
    }
}
