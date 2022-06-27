package de.eldritch.turtle.server.moderation;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.moderation.interactions.SupportListener;
import de.eldritch.turtle.server.moderation.ticket.ApplyTicket;
import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.entities.Category;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ModerationService {
    private final ConcurrentHashMap<Long, SupportTicket> ticketSupportCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ApplyTicket>   ticketApplyCache   = new ConcurrentHashMap<>();

    private final NestedToggleLogger logger = new NestedToggleLogger("ModerationService", TurtleServer.LOGGER);

    private Category supportCategory;

    private final SupportListener supportListener;

    public ModerationService() {
        this.retrieveSupportCategory();

        this.reloadTicketSupport();
        this.reloadTicketApply();

        // assign and register the general support listener
        this.supportListener = new SupportListener(this);
        Main.singleton.getDiscordService().getJDA().getEventManager().register(supportListener);
    }

    public void shutdown() {
        // unregister the general support listener
        Main.singleton.getDiscordService().getJDA().getEventManager().unregister(supportListener);

        for (SupportTicket ticket : ticketSupportCache.values())
            saveTicketSupport(ticket);

        for (ApplyTicket ticket : ticketApplyCache.values())
            saveTicketApply(ticket);
    }

    /* ----- ----- ----- */

    public void retrieveSupportCategory() {
        String id = Main.singleton.getConfig().getString("moderation.support.category", "null");
        this.supportCategory = Main.singleton.getDiscordService().getJDA().getCategoryById(id);
    }

    public void reloadTicketSupport() {
        List<SupportTicket> tickets = Main.singleton.getDataService().getSupportTickets();

        if (tickets != null) {
            // invalidate cache
            ticketSupportCache.clear();

            // feed ticket objects to cache
            for (SupportTicket ticket : tickets)
                register(ticket);
        } else {
            logger.log(Level.WARNING, "Could not reload support tickets!");
        }
    }

    public void reloadTicketApply() {
        List<ApplyTicket> tickets = Main.singleton.getDataService().getApplyTickets();

        if (tickets != null) {
            // invalidate cache
            ticketApplyCache.clear();

            // feed ticket objects to cache
            for (ApplyTicket ticket : tickets)
                register(ticket);
        } else {
            logger.log(Level.WARNING, "Could not reload application tickets!");
        }
    }

    /* ----- ----- ----- */

    public void saveTicketSupport(@NotNull SupportTicket ticket) {
        Main.singleton.getDataService().updateSupportTicket(
                ticket.getID(),
                ticket.getUser().getID(),
                ticket.getState(),
                ticket.getChannel().getIdLong()
        );
    }

    public void saveTicketApply(@NotNull ApplyTicket ticket) {
        // TODO: save to DB
    }

    /* ----- ----- ----- */

    public @Nullable SupportTicket getTicketSupport(long turtleID) {
        return ticketSupportCache.get(turtleID);
    }

    public @Nullable ApplyTicket getTicketApply(long turtleID) {
        return ticketApplyCache.get(turtleID);
    }

    /* ----- ----- ----- */

    /**
     * Registers a support ticket to the backing database and puts it in the cache.
     * @param ticket The ticket object.
     */
    void register(@NotNull SupportTicket ticket) {
        // put ticket into cache
        ticketSupportCache.put(ticket.getID(), ticket);

        // put ticket into database
        saveTicketSupport(ticket);
    }

    /**
     * Registers an application ticket to the backing database and puts it in the cache.
     * @param ticket The ticket object.
     */
    void register(@NotNull ApplyTicket ticket) {
        // put ticket into cache
        ticketApplyCache.put(ticket.getID(), ticket);

        // put ticket into database
        saveTicketApply(ticket);
    }

    /* ----- ----- ----- */

    public Category getSupportCategory() {
        return supportCategory;
    }
}
