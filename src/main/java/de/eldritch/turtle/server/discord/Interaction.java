package de.eldritch.turtle.server.discord;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.moderation.ModerationService;
import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import de.eldritch.turtle.server.util.MiscUtil;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Interaction {
    private final DiscordService service;
    private final String key;

    private final NestedToggleLogger logger;

    public Interaction(DiscordService service, @NotNull String key) {
        this.service = service;
        this.key = key;

        this.logger = new NestedToggleLogger("INTERACTION / " + key, service.getLogger());
    }

    public @NotNull String getKey() {
        return key;
    }

    /* ----- */

    public final void execute(@NotNull DataObject context, @Nullable Runnable onSuccess, @Nullable Consumer<? super Throwable> onFailure) {
        try {
            this.execute(context);

            if (onSuccess != null)
                onSuccess.run();
        } catch (Throwable t) {
            if (onFailure != null)
                onFailure.accept(t);
        }
    }

    protected abstract void execute(@NotNull DataObject context) throws Throwable;

    /* ----- */

    public static void onInteraction(@NotNull String key, @NotNull DataObject context, @Nullable Runnable onSuccess, @Nullable Consumer<? super Throwable> onFailure) {
        Interaction interaction = Main.singleton.getDiscordService().getInteraction(key);

        if (interaction == null) {
            if (onFailure != null)
                onFailure.accept(new NullPointerException("Could not match interaction key '" + key + "'."));
        } else {
            interaction.execute(context, onSuccess, onFailure);
        }
    }

    public static Set<Interaction> buildInteractions(DiscordService service) {
        return Set.of(
                new Interaction(service, "support-ticket-open") {
                    @Override
                    protected void execute(@NotNull DataObject context) throws Throwable {
                        final ModerationService moderationService = Main.singleton.getModerationService();

                        // TODO: check if the user already has open tickets

                        for (Collection<SupportTicket> supportTickets : Set.of(moderationService.getSupportTicketsView())) {

                        }

                        String title = MiscUtil.cut(context.getString("username"), 8);

                        // TODO: replace this - make a copy of the moderators' channel and modify permissions
                        ChannelAction<TextChannel> channelAction = moderationService.getSupportCategory().createTextChannel("ticket-" + title);

                        // TODO
                    }
                }
                // TODO
        );
    }
}
