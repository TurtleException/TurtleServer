package de.eldritch.turtle.server.moderation.ticket;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.moderation.interactions.SupportChannelHandler;
import de.eldritch.turtle.server.user.User;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class SupportTicket extends Ticket {
    public static class State {
        public static final int UNDEFINED = 0;
        /**
         * The ticket has been processed successfully or the issue resolved itself otherwise.
         */
        public static final int CLOSED = 1;
        /**
         * The ticket is currently being processed.
         */
        public static final int OPEN = 2;
        /**
         * The ticket cannot be processed. This might be because it conflicts with another ticket.
         */
        public static final int LOCKED = 3;
        /**
         * The ticket is temporarily unused. This will happen if no activity occurs on an open ticket for some time.
         */
        public static final int IDLE = 4;
    }

    private int state;

    private final TextChannel channel;

    SupportTicket(long turtleID, @NotNull User user, int state, @NotNull TextChannel channel) {
        super(turtleID, user);

        this.state = state;
        this.channel = channel;

        // assign and register the ticket handler
        Main.singleton.getDiscordService().getJDA().getEventManager().register(new SupportChannelHandler(this));
    }

    /* ----- ----- ----- */

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
