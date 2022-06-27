package de.eldritch.turtle.server.discord;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class StaticManager {
    private final DiscordService service;

    private final ConcurrentHashMap<String, StaticMessage> staticMessages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ActionRow>     actionRows     = new ConcurrentHashMap<>();

    StaticManager(DiscordService service) {
        this.service = service;
    }

    /* ----- ----- ----- */

    public void register(@NotNull StaticMessage message) {
        DiscordService.LOGGER_EXT_DISC.log(Level.INFO, "Registered static message '" + message.getKey() + "'.");
        staticMessages.put(message.getKey(), message);
    }

    public void unregisterMessage(@NotNull String key, boolean deleteMessage) {
        DiscordService.LOGGER_EXT_DISC.log(Level.INFO,
                staticMessages.remove(key) != null
                        ? "Unregistered static message '" + key + "'."
                        : "Could not unregister static message '" + key + ": message does not exist.");
    }

    public @Nullable StaticMessage getMessage(@NotNull String key) {
        return staticMessages.get(key);
    }

    /* ----- ----- ----- */

    void registerActionRow(@NotNull String key, @Nullable ActionRow actionRow) {
        if (actionRow != null)
            actionRows.put(key, actionRow);
        else
            actionRows.remove(key);
    }

    @Nullable ActionRow getActionRow(@NotNull String key) {
        return actionRows.get(key);
    }
}
