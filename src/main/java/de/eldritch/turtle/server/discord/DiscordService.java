package de.eldritch.turtle.server.discord;

import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.util.MiscUtil;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import de.eldritch.turtle.server.util.logging.logback.JavaLoggingAppender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * Basically the main interface to the Discord API and everything directly related to it.
 * <p>This class holds the main {@link JDA} instance that is connected to the Discord bot equivalent of this
 * application. All traffic to this bot should always happen through said instance to ensure correct handling of rate
 * limits.
 * <p>One extra functionality of this class is the management of {@link StaticMessage StaticMessages}. See the
 * corresponding documentation for further information.
 */
public class DiscordService {
    /**
     * A rather blunt solution to the problem that JDA only logs via SLF4J or logback - both of which don't support a
     * simple way of translating logs to the <code>java.logging</code> library.
     * <p>This logger, combined with the {@link JavaLoggingAppender}, will attempt to catch and translate log records.
     */
    public static final NestedToggleLogger LOGGER_INTERNAL = new NestedToggleLogger("JDA-internal", TurtleServer.LOGGER);
    /**
     * A logging adapter for everything that is related to the DiscordService but does not have access to the internal
     * {@link NestedToggleLogger}. For instance a {@link StaticMessage}.
     */
    public static final NestedToggleLogger LOGGER_EXT_DISC = new NestedToggleLogger("DiscordService-EXT", TurtleServer.LOGGER);

    /**
     * The internal logger of this service.
     */
    private final NestedToggleLogger logger = new NestedToggleLogger("DiscordService", TurtleServer.LOGGER);

    /**
     * The actual JDA instance that will be the main (and should be the only) connection to the primary Discord Bot.
     * <p>This instance is not final as it can be reassigned to effectively reload the Discord API connection.
     * @see DiscordService#builder
     */
    private JDA jda;
    private final JDABuilder builder = JDABuilder.create(
            GatewayIntent.DIRECT_MESSAGES // just a dummy
            // TODO
    );

    private final ConcurrentHashMap<String, Interaction> interactionIndex = new ConcurrentHashMap<>();

    public DiscordService() throws LoginException {
        this.init();
    }

    public void init() throws LoginException {
        /* --- JDA */
        jda = builder.build();

        /* --- INTERACTIONS */
        interactionIndex.clear();
        for (Interaction interaction : Interaction.buildInteractions(this))
            interactionIndex.put(interaction.getKey(), interaction);
    }

    public void shutdown() {
        logger.log(Level.INFO, "Received shutdown command!");

        jda.shutdown();
        try {
            MiscUtil.await(() -> jda.getStatus() == JDA.Status.SHUTDOWN, 10, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException e) {
            logger.log(Level.WARNING, "Failed to await status.", e);
        }

        if (jda.getStatus() != JDA.Status.SHUTDOWN) {
            logger.log(Level.INFO, "Attempting to force shutdown...");
            jda.shutdownNow();

            try {
                MiscUtil.await(() -> jda.getStatus() == JDA.Status.SHUTDOWN, 2, TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException e) {
                logger.log(Level.WARNING, "Failed to await status.", e);
            } finally {
                logger.log(Level.INFO, "JDA is now shut down.");
            }
        } else {
            logger.log(Level.INFO, "JDA is now shut down.");
        }

        jda = null;
    }

    /* ----- ----- ----- */

    public @Nullable Interaction getInteraction(@NotNull String key) {
        return interactionIndex.get(key);
    }

    /* ----- ----- ----- */

    public JDA getJDA() {
        return jda;
    }

    public NestedToggleLogger getLogger() {
        return logger;
    }
}
