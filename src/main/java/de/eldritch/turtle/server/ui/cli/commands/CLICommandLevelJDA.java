package de.eldritch.turtle.server.ui.cli.commands;

import de.eldritch.turtle.server.util.logging.logback.JavaLoggingAppender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class CLICommandLevelJDA extends CLICommandLevel {
    @Override
    protected void get() {
        out("JDA log level is currently set to " + JavaLoggingAppender.jdaLevelFilter.getName());
    }

    @Override
    protected void set(Level level) {
        if (level != null) {
            JavaLoggingAppender.jdaLevelFilter = level;
            out("JDA log level has been set to " + level.getName());
        }
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "level-jda",
                "level-jda list",
                "level-jda <level>"
        );
    }
}
