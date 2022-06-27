package de.eldritch.turtle.server.ui.cli;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Responsible for all input the user provides via {@link System#in}. {@link TurtleServer} will check for input via a
 * simple {@link Scanner} in its' main loop.
 */
public class CLIReceiver {
    private static final int DIALOGUE_NONE = 0;
    private static final int DIALOGUE_QUIT = 1;

    /**
     * Simple way of telling which dialogue is currently presented to the user.
     * <p>See "<code>DIALOGUE_...</code>" constants above for more information.
     */
    private int dialogue = 0;


    /**
     * Called by {@link TurtleServer} when the user inputs a {@link String} and terminates the line while the program is
     * in the <code>RUNNING</code> state.
     * @param line String representation of the input line.
     */
    public void receive(String line) {
        if (line == null)    return;
        if (line.equals("")) return;

        switch (line.toLowerCase()) {
            case "y" -> dialogueYes();
            case "n" -> dialogueNo();
            case "p" -> pauseOutput(!TurtleServer.LOGGER.isActive());
            case "q", "quit", "e", "exit" -> {
                dialogue = DIALOGUE_QUIT;
                TurtleServer.LOGGER.setActive(false);
                System.out.println("Do you want to quit? [Y/N]");
            }
            default -> {
                // check commands
                String[] tokenArray = line.stripLeading().split(" ");
                if (tokenArray.length > 0) {
                    String[] args = {};

                    /* skip the initial command when passing arguments */
                    if (tokenArray.length > 1)
                        args = Arrays.copyOfRange(tokenArray, 1, tokenArray.length);

                    if (!CLICommands.onCommand(tokenArray[0], args, line)) {
                        TurtleServer.LOGGER.log(Level.WARNING, "Unknown command: '" + tokenArray[0] + "'!");
                    }
                }
            }
        }
    }

    /**
     * Called when the user inputs a <code>y</code> (case-insensitive).
     */
    private void dialogueYes() {
        // guard valid dialogue state
        if (dialogue == DIALOGUE_NONE) return;

        switch (dialogue) {
            case DIALOGUE_QUIT -> {
                pauseOutput(false);
                dialogue = DIALOGUE_NONE;
                Main.singleton.exit();
            }
        }
    }

    /**
     * Called when the user inputs a <code>n</code> (case-insensitive).
     */
    private void dialogueNo() {
        // guard valid dialogue state
        if (dialogue == DIALOGUE_NONE) return;

        switch (dialogue) {
            case DIALOGUE_QUIT -> {
                pauseOutput(false);
                dialogue = DIALOGUE_NONE;
            }
        }
    }

    /**
     * Pauses the output to {@link System#out} of {@link TurtleServer#LOGGER}.
     * @param b <code>true</code> to pause, <code>false</code> to resume output.
     */
    private void pauseOutput(boolean b) {
        TurtleServer.LOGGER.setActive(!b);
    }
}
