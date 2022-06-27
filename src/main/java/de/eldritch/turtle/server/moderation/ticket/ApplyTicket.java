package de.eldritch.turtle.server.moderation.ticket;

import de.eldritch.turtle.server.project.Project;
import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;

public class ApplyTicket extends Ticket {
    private final Project project;

    public ApplyTicket(long turtleID, @NotNull User user, @NotNull Project project) {
        super(turtleID, user);
        this.project = project;
    }
}
