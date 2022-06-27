package de.eldritch.turtle.server.util;

public class TurtleUtil {
    /* ----- ----- ----- */

    private static long lastID;

    public static synchronized long newID() {
        final long timestamp = System.currentTimeMillis();

        if (lastID >= timestamp)
            lastID = timestamp + 1;

        return lastID;
    }

    /* ----- ----- ----- */
}
