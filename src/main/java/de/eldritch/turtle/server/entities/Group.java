package de.eldritch.turtle.server.entities;

import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;

import javax.management.relation.Role;

/**
 * A group is basically a role or rank that can be assigned to a {@link User} (<code>n:m</code>-relationship).
 * <p>The name was chosen mainly to prevent confusion with equivalents like {@link Role}.
 */
public final class Group implements Turtle {
    private final long turtleID;

    private final String title;
    private int permissionLevel;
    private boolean isTeam;

    public Group(long turtleID, @NotNull String title, int permissionLevel) {
        this.turtleID = turtleID;

        this.title = title;
        this.permissionLevel = permissionLevel;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public boolean isTeam() {
        return this.isTeam;
    }

    public void setIsTeam(boolean isTeam) {
        this.isTeam = isTeam;
    }

    @Override
    public long getID() {
        return turtleID;
    }
}
