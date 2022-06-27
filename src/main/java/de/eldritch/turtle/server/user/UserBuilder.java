package de.eldritch.turtle.server.user;

import de.eldritch.turtle.server.entities.Group;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <b>NOTE</b>: This implementation is currently not thread-safe!
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class UserBuilder {
    private Long        turtle   = null;
    private UserService uService = null;

    private UUID       minecraftPrimary   = null;
    private List<UUID> minecraftSecondary = new ArrayList<>();

    private Long       discordPrimary   = null;
    private List<Long> discordSecondary = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    public UserBuilder() { }

    /* ----- ----- ----- */

    public @NotNull User build() throws IllegalArgumentException {
        try {
            if (turtle == null)
                throw new IllegalArgumentException("Turtle ID may not be null.");

            final User user = new User(turtle, uService);

            user.setDiscordPrimary(discordPrimary);
            user.setMinecraftPrimary(minecraftPrimary);

            for (Group group : groups)
                user.addGroup(group);

            for (UUID minecraftSecondaryObj : minecraftSecondary)
                user.addMinecraftSecondary(minecraftSecondaryObj);

            for (Long discordSecondaryObj : discordSecondary)
                user.addDiscordSecondary(discordSecondaryObj);

            uService.register(user);

            return user;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not build User due to an exception.", e);
        }
    }

    /* ----- ----- ----- */

    public UserBuilder setTurtle(long turtle) {
        this.turtle = turtle;
        return this;
    }

    public UserBuilder setUserService(UserService uService) {
        this.uService = uService;
        return this;
    }

    public UserBuilder setDiscordPrimary(@Nullable Long snowflake) {
        this.discordPrimary = snowflake;
        return this;
    }

    public UserBuilder addDiscordSecondary(@NotNull Long snowflake) {
        this.discordSecondary.add(snowflake);
        return this;
    }

    public UserBuilder removeDiscordSecondary(Long snowflake) {
        this.discordSecondary.remove(snowflake);
        return this;
    }

    public UserBuilder setMinecraftPrimary(@Nullable UUID uuid) {
        this.minecraftPrimary = uuid;
        return this;
    }

    public UserBuilder addMinecraftSecondary(@NotNull UUID uuid) {
        this.minecraftSecondary.add(uuid);
        return this;
    }

    public UserBuilder removeMinecraftSecondary(UUID uuid) {
        this.minecraftSecondary.remove(uuid);
        return this;
    }
}
