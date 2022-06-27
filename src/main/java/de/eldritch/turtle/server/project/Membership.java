package de.eldritch.turtle.server.project;

import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Connects a {@link User} and a {@link Project}.
 */
public class Membership {
    private final Project project;
    private final User    user;

    public static class State {
        public static final int UNDEFINED = 0;
        public static final int MEMBER    = 1;
        public static final int PENDING   = 2;
        public static final int BANNED    = 3;
    }

    private int    state;
    /**
     * Additional data for this relationship in JSON (optional).
     */
    @Nullable
    private String data;

    public Membership(@NotNull Project project, @NotNull User user, int state, @Nullable String data) {
        this.project = project;
        this.user = user;
        this.state = state;
        this.data = data;
    }

    /* ----- ----- ----- */

    public @NotNull Project getProject() {
        return project;
    }

    public @NotNull User getUser() {
        return user;
    }

    public int getState() {
        return state;
    }

    public @Nullable String getData() {
        return data;
    }

    /* ----- ----- ----- */

    public void setState(int state) {
        this.state = state;
    }

    public void setData(@Nullable String data) {
        this.data = data;
    }
}
