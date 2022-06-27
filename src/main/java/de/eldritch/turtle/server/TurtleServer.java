package de.eldritch.turtle.server;

import de.eldritch.turtle.server.data.DataService;
import de.eldritch.turtle.server.discord.DiscordService;
import de.eldritch.turtle.server.moderation.ModerationService;
import de.eldritch.turtle.server.project.ProjectService;
import de.eldritch.turtle.server.ui.cli.CLIReceiver;
import de.eldritch.turtle.server.user.UserService;
import de.eldritch.turtle.server.util.ConfigUtil;
import de.eldritch.turtle.server.util.Status;
import de.eldritch.turtle.server.util.logging.LogUtil;
import de.eldritch.turtle.server.util.logging.SimpleFormatter;
import de.eldritch.turtle.server.util.logging.SystemOutputToggleLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * The actual main class of the program.
 * @see Main
 */
public class TurtleServer {
    private final Status status = new Status();

    public static final SystemOutputToggleLogger LOGGER = new SystemOutputToggleLogger("SERVER");

    /**
     * Directory in which the JAR is located.
     */
    public static final File DIR;
    static {
        File f = null;
        try {
            f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            System.out.println("Failed to declare directory.");
            e.printStackTrace();
        }
        DIR = f;
    }

    // Add FileHandler to LOGGER
    static {
        try {
            LOGGER.addHandler(LogUtil.getFileHandler(new SimpleFormatter()));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not register FileHandler", e);
        }
    }

    /* ----- ----- ----- */

    private YamlConfiguration config;

    /* ----- ----- ----- */

    private DataService       dataService;
    private DiscordService    discordService;
    private UserService       userService;
    private ProjectService    projectService;
    private ModerationService moderationService;

    @SuppressWarnings("FieldCanBeLocal")
    private CLIReceiver cliReceiver;

    /* ----- ----- ----- */

    public void run() throws Exception {
        status.set(Status.INIT);

        config = ConfigUtil.getConfig("config", "config");
        ConfigUtil.validateVersion();

        LOGGER.log(Level.INFO, "Initializing DataService...");
        dataService = new DataService();

        LOGGER.log(Level.INFO, "Initializing DiscordService...");
        discordService = new DiscordService();

        LOGGER.log(Level.INFO, "Initializing UserService...");
        userService = new UserService();

        LOGGER.log(Level.INFO, "Initializing ProjectService...");
        projectService = new ProjectService();

        LOGGER.log(Level.INFO, "Initializing ModerationService...");
        moderationService = new ModerationService();

        LOGGER.log(Level.INFO, "Initializing Receiver...");
        cliReceiver = new CLIReceiver();

        /* ----- RUNNING ----- */

        LOGGER.log(Level.INFO, "Startup done!");

        Scanner scanner = new Scanner(System.in);

        status.set(Status.RUNNING);
        while (status.get() == Status.RUNNING) {
            String line = scanner.nextLine();
            cliReceiver.receive(line);
        }

        LOGGER.log(Level.WARNING, "Main loop has been interrupted.");

        this.shutdown();
    }

    /**
     * Notifies the program to exit the main loop and shut down.
     */
    public void exit() {
        status.set(Status.STOPPING);
    }

    /**
     * Indicates whether the main loop is currently active.
     * @return <code>true</code> if the main loop is running.
     */
    public boolean isRunning() {
        return status.get() == Status.RUNNING;
    }


    /**
     * Await execution of final tasks, proper shutdown of all active tasks and suspend all active threads.
     */
    private void shutdown() {
        if (status.get() <= Status.RUNNING)
            throw new IllegalStateException("Cannot shutdown while main loop is not yet or still running. Call exit() first!");

        LOGGER.log(Level.INFO, "Shutting down...");

        LOGGER.log(Level.INFO, "Notifying ModerationService...");
        moderationService.shutdown();

        LOGGER.log(Level.INFO, "Notifying ProjectService...");
        projectService.shutdown();

        LOGGER.log(Level.INFO, "Notifying UserService...");
        userService.shutdown();

        LOGGER.log(Level.INFO, "Notifying DiscordService...");
        discordService.shutdown();

        LOGGER.log(Level.INFO, "Notifying DataService...");
        dataService.shutdown();

        LOGGER.log(Level.INFO, "Saving config...");
        try {
            ConfigUtil.saveConfig(config, "config");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not save config due to an exception.", e);
        }

        LOGGER.log(Level.ALL, "OK bye.");
        LOGGER.shutdown();

        System.exit(0);
    }

    /* ----- ----- ----- */

    public YamlConfiguration getConfig() {
        return config;
    }

    /* ----- ----- ----- */

    public DataService getDataService() {
        return dataService;
    }

    public DiscordService getDiscordService() {
        return discordService;
    }

    public UserService getUserService() {
        return userService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public ModerationService getModerationService() {
        return moderationService;
    }
}
