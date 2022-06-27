package de.eldritch.turtle.server.moderation.interactions;

import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

// TODO
// NOTE: don't forget to check the ticket state for each action!
//  -> maybe do this with multiple handlers (performance improvement)
public class SupportChannelHandler extends ListenerAdapter {
    private final SupportTicket ticket;

    public SupportChannelHandler(@NotNull SupportTicket ticket) {
        this.ticket = ticket;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {

    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {

    }

    @Override
    public void onMessageBulkDelete(@NotNull MessageBulkDeleteEvent event) {

    }

    /* ----- */

    private boolean isTicketChannel(GuildMessageChannel channel) {
        return channel.getIdLong() == ticket.getChannel().getIdLong();
    }
}
