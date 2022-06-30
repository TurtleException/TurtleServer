package de.eldritch.turtle.server.user;

import com.google.common.collect.Sets;
import de.eldritch.turtle.server.entities.Group;
import de.eldritch.turtle.server.entities.Turtle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class User implements Turtle {
    private final long turtleID;

    /**
     * The UserService responsible for this User.
     */
    private final UserService uService;

    /**
     * The main Discord User.
     */
    @Nullable
    private Long discordPrimary;
    /**
     * Possible secondary Discord Users.
     */
    private final Set<Long> discordSecondary = Sets.newConcurrentHashSet();

    /**
     * The main Minecraft Player UUID.
     */
    @Nullable
    private UUID minecraftPrimary;
    /**
     * Possible secondary Minecraft Player UUIDs.
     */
    private final Set<UUID> minecraftSecondary = Sets.newConcurrentHashSet();

    private final Set<Group> groups = Sets.newConcurrentHashSet();

    /**
     * Last known name of the user (focus on Discord).
     */
    private String name;

    public User(long turtleID, UserService uService) {
        this.turtleID = turtleID;
        this.uService = uService;
    }

    /* ----- ----- ----- */

    public @Nullable Long getDiscordPrimary() {
        return discordPrimary;
    }

    public @NotNull Set<Long> getDiscordSecondary() {
        return discordSecondary;
    }

    public @Nullable UUID getMinecraftPrimary() {
        return minecraftPrimary;
    }

    public @NotNull Set<UUID> getMinecraftSecondary() {
        return minecraftSecondary;
    }

    public @NotNull Set<Group> getGroups() {
        return groups;
    }

    public @NotNull String getName() {
        return name != null ? name : "unknown";
    }

    /* ----- ----- ----- */

    public void setDiscordPrimary(@Nullable Long discordPrimary) {
        this.discordPrimary = discordPrimary;
    }

    public void setMinecraftPrimary(@Nullable UUID minecraftPrimary) {
        this.minecraftPrimary = minecraftPrimary;
    }

    public void addDiscordSecondary(long discordSecondary) {
        this.discordSecondary.add(discordSecondary);
    }

    public void removeDiscordSecondary(long discordSecondary) {
        this.discordSecondary.remove(discordSecondary);
    }

    public void addMinecraftSecondary(@NotNull UUID uuid) {
        this.minecraftSecondary.add(uuid);
    }

    public void removeMinecraftSecondary(@NotNull UUID uuid) {
        this.minecraftSecondary.remove(uuid);
    }

    public void addGroup(@NotNull Group group) {
        this.groups.add(group);
    }

    public void removeGroup(@NotNull Group group) {
        this.groups.remove(group);
    }

    /* ----- ----- ----- */

    public int getPermissionLevel() {
        int level = 0;

        for (Group group : groups)
            level = Math.max(group.getPermissionLevel(), level);

        return level;
    }

    public long getID() {
        return turtleID;
    }
}
