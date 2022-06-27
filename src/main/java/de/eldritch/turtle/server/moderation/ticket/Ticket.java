package de.eldritch.turtle.server.moderation.ticket;

import de.eldritch.turtle.server.entities.Turtle;
import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;

public abstract class Ticket implements Turtle {
    protected final long turtleID;

    @NotNull
    protected User user;

    protected Ticket(long turtleID, @NotNull User user) {
        this.turtleID = turtleID;
        this.user = user;
    }

    @Override
    public final long getID() {
        return turtleID;
    }

    public @NotNull User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }
}
