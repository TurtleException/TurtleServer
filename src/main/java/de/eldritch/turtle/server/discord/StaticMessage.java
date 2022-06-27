package de.eldritch.turtle.server.discord;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.util.ConfigUtil;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class StaticMessage {
    protected final String key;

    private MessageChannel channel;
    private ArrayList<Message> messages = new ArrayList<>();

    private YamlConfiguration config;

    public StaticMessage(@NotNull String key) throws IOException, InvalidConfigurationException {
        this.key = key;

        this.init();

        this.updateMessages();
    }

    public void init() throws IOException, InvalidConfigurationException {
        this.config = ConfigUtil.getConfig("statics/" + key + "/config", null);

        String channelID = config.getString("channel", "null");

        this.channel = Main.singleton.getDiscordService().getJDA().getTextChannelById(channelID);
    }

    /* ----- ----- ----- */

    public void indexExistingMessages() {
        if (channel == null) {
            messages = new ArrayList<>();
            return;
        }

        List<String> messageIDs = config.getStringList("cache.messages");

        /*
         * If at least one message could not be retrieved due to a RuntimeException this boolean will be set to true,
         * indicating that all existing, indexed messages should be deleted at the end to re-send them in the correct
         * order of appearance.
         */
        boolean error = false;

        for (String messageID : messageIDs) {
            if (messageID == null) continue;

            try {
                // TODO: is that safe here?
                messages.add(channel.retrieveMessageById(messageID).complete());
            } catch (RuntimeException e) {
                DiscordService.LOGGER_EXT_DISC.log(Level.WARNING, "Could not retrieve discord message " + messageID + " for static message " + key, e);
                if (!error)
                    DiscordService.LOGGER_EXT_DISC.log(Level.WARNING, "Marking for deletion and re-send...");

                // see further above for more info on this
                error = true;
            }
        }

        if (error) {
            // delete messages to re-send later (see above)
            this.deleteMessages();
        }
    }

    /* ----- ----- ----- */

    public void updateMessages() {
        this.indexExistingMessages();

        List<StaticMessageParser.Content> contents;
        try {
            contents = StaticMessageParser.parseContent(key);

            if (this.editMessages(contents))
                this.sendMessages(contents);
        } catch (Exception e) {
            DiscordService.LOGGER_EXT_DISC.log(Level.WARNING, "Could not update static message " + key + " due to an exception.", e);
        }
    }

    /**
     * @return true, if correctly updating all messages failed.
     */
    public boolean editMessages(@NotNull List<StaticMessageParser.Content> contents) {
        ArrayList<MessageAction> restActions = new ArrayList<>();

        if (contents.size() != messages.size()) return true;

        for (StaticMessageParser.Content content : contents) {
            MessageBuilder builder = content.messageBuilder();
            // TODO: nononono
            MessageAction  action  = channel.sendMessage(builder.build());

            if (content.files() != null && content.files().length > 0) {
                for (File file : content.files()) {
                    action = action.addFile(file);
                }

                // TODO: ActionRows
            }

            restActions.add(action);
        }

        // the correct order should be preserved by JDA
        for (MessageAction restAction : restActions) {
            restAction.queue();
        }

        return false;
    }

    public void sendMessages(@NotNull List<StaticMessageParser.Content> contents) {
        this.deleteMessages();

        ArrayList<MessageAction> restActions = new ArrayList<>();

        for (StaticMessageParser.Content content : contents) {
            MessageBuilder builder = content.messageBuilder();
            MessageAction  action  = channel.sendMessage(builder.build());

            if (content.files() != null && content.files().length > 0) {
                for (File file : content.files()) {
                    action = action.addFile(file);
                }
            }

            if (content.actionRowKeys() != null && content.actionRowKeys().length > 0) {
                ArrayList<ActionRow> actionRows = new ArrayList<>();

                for (String str : content.actionRowKeys()) {
                    ActionRow actionRow = Main.singleton.getDiscordService().getStatics().getActionRow(str);

                    if (actionRow == null) {
                        DiscordService.LOGGER_EXT_DISC.log(Level.WARNING, "ActionRowKey '" + str + "' does not exist. (message " + key + ")");
                        continue;
                    }

                    actionRows.add(actionRow);
                }

                action = action.setActionRows(actionRows);
            }

            restActions.add(action);
        }

        // the correct order should be preserved by JDA
        for (MessageAction restAction : restActions) {
            restAction.queue();
        }
    }

    public void deleteMessages() {
        for (Message message : messages) {
            try {
                // TODO: is that safe here?
                message.delete().complete();
            } catch (RuntimeException e) {
                DiscordService.LOGGER_EXT_DISC.log(Level.WARNING, "Encountered a RuntimeException while attempting to delete discord message " + message.getId() + " for static message " + key, e);
            }
        }

        // clear cache
        config.set("cache.messages", List.of(""));
    }

    /* ----- ----- ----- */

    public final @NotNull String getKey() {
        return key;
    }
}
