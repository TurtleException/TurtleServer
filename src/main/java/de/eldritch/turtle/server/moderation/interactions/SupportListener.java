package de.eldritch.turtle.server.moderation.interactions;

import de.eldritch.turtle.server.discord.Interaction;
import de.eldritch.turtle.server.moderation.ModerationService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class SupportListener extends ListenerAdapter {
    private final ModerationService service;

    public SupportListener(@NotNull ModerationService service) {
        this.service = service;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        // TODO: this is a dummy
        Interaction.onInteraction("support-ticket-open", DataObject.empty(), null, null);
    }
}
