package de.eldritch.turtle.server.discord;

import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.utils.data.DataArray;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

public class StaticMessage {
    private final DiscordService service;
    private final String key;

    private final NestedToggleLogger logger;

    private ConcurrentHashMap<String, Long> messageCache = new ConcurrentHashMap<>();

    public StaticMessage(@NotNull DiscordService service, @NotNull String key) {
        this.service = service;
        this.key = key;

        this.logger = new NestedToggleLogger("STATIC / " + key, service.getLogger());

        if (this.checkRefresh())
            this.update();
    }

    /**
     * Checks whether the local data differs from the cached discord messages.
     * @return true, if the data does not match and the static message should be updated.
     */
    public boolean checkRefresh() {

    }

    /**
     * Retrieves the local data of this static message and provides it as a {@link DataArray}.
     */
    public DataArray retrieveData() throws FileNotFoundException {
        File file = new File(TurtleServer.DIR, "statics/" + key + ".json");
        return DataArray.fromJson(new FileReader(file));
    }

    public void update() {

    }
}
