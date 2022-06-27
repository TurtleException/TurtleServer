package de.eldritch.turtle.server.ui.cli;

import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.ui.cli.commands.CLICommandHelp;
import de.eldritch.turtle.server.ui.cli.commands.CLICommandLevel;
import de.eldritch.turtle.server.ui.cli.commands.CLICommandLevelJDA;

import java.util.HashMap;
import java.util.Map;

/**
 * Both a collection of all available CLI commands and responsible for the basic logic behind command management.
 * @see CLIReceiver
 * @see CLICommands#onCommand(String, String[], String)
 */
public class CLICommands {
    /**
     * Static map of commands with their corresponding {@link String} representation as key and the specific
     * {@link CLICommand} implementation as value.
     */
    private static final HashMap<String, CLICommand> commands = new HashMap<>();

    // class initializer to have commands available statically
    static {
        commands.put("help"     , new CLICommandHelp());
        commands.put("level"    , new CLICommandLevel());
        commands.put("level-jda", new CLICommandLevelJDA());
    }

    /**
     * This method gets invoked by {@link CLIReceiver#receive(String)} when no other method of input can be applied.
     * @param cmd String representation of the command.
     * @param args Arguments, excluding the content of cmd.
     * @param raw Raw input string provided by {@link TurtleServer} main loop.
     * @return Whether a command could be matched to the cmd string.
     */
    public static synchronized boolean onCommand(String cmd, String[] args, String raw) {
        CLICommand command = commands.get(cmd);

        if (command != null) {
            command.onInvoke(args, raw);
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, CLICommand> getCommands() {
        return Map.copyOf(commands);
    }
}