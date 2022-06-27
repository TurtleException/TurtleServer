package de.eldritch.turtle.server.ui.cli.commands;

import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.ui.cli.CLICommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * Command to set the {@link Level} of {@link TurtleServer#LOGGER}.
 */
public class CLICommandLevel implements CLICommand {
    @Override
    public void onInvoke(String[] args, String raw) {
        if (args.length < 1) {
            get();
            return;
        }

        Level level = null;
        String str = args[0].toUpperCase();

        switch (str) {
            case "LIST"            -> list();
            case "ALL"             -> level = Level.ALL;
            case "FINEST"          -> level = Level.FINEST;
            case "FINER"           -> level = Level.FINER;
            case "FINE"            -> level = Level.FINE;
            case "INFO", "DEFAULT" -> level = Level.INFO;
            case "WARNING"         -> level = Level.WARNING;
            case "SEVERE"          -> level = Level.SEVERE;
            case "OFF"             -> level = Level.OFF;
            default -> out("Unknown argument: '" + args[0] + "'.");
        }

        set(level);
    }

    protected void get() {
        out("Log level is currently set to " + TurtleServer.LOGGER.getLevel().getName());
    }

    protected void set(Level level) {
        if (level != null) {
            TurtleServer.LOGGER.setLevel(level);
            out("Log level has been set to " + level.getName());
        }
    }

    /**
     * Lists all available log levels and prints them to the console.
     */
    private void list() {
        out("Log levels:  < ALL | FINEST | FINER | FINE | INFO | DEFAULT | WARNING | SEVERE | OFF >");
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "level",
                "level list",
                "level <level>"
        );
    }
}
