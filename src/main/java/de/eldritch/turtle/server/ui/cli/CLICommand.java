package de.eldritch.turtle.server.ui.cli;

import de.eldritch.turtle.server.TurtleServer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;

/**
 * An executable CLI command.
 */
public interface CLICommand {
    /**
     * Called to invoke the command with the given arguments.
     * @param args Command arguments.
     */
    void onInvoke(String[] args, String raw);

    /**
     * Provides a {@link List} of {@link String Strings} explaining how to use the command.
     * @return Usage help.
     */
    @NotNull List<String> usage();

    /**
     * Logs a message with level {@link Level#WARNING} in the global logger. This is a shortcut for easier
     * implementation.
     * @param msg Log message.
     */
    default void logWarn(String msg) {
        TurtleServer.LOGGER.log(Level.WARNING, msg);
    }

    /**
     * Logs a message with level {@link Level#WARNING} in the global logger. This is a shortcut for easier
     * implementation.
     * @param msg Log message.
     * @param thrown Throwable to append to the log record.
     */
    default void logWarn(String msg, Throwable thrown) {
        TurtleServer.LOGGER.log(Level.WARNING, msg, thrown);
    }

    default void out(String msg) {
        String time = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").format(Instant.now().atZone(ZoneId.of("UTC")));
        System.out.println("[" + time + "]: [" + this.getClass().getSimpleName() + "] " + msg);
    }
}