package de.eldritch.turtle.server.project;

import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Project {
    public static class State {
        public static final int UNDEFINED    = 0;
        public static final int CONCEPT      = 1;
        public static final int PLANNING     = 2;
        public static final int PENDING      = 3;
        public static final int APPLICATION  = 4;
        public static final int RUNNING      = 5;
        public static final int DISCONTINUED = 6;
    }

    /* ----- ----- ----- */

    private final    int     id;
    private @NotNull String  name;
    private          int     state;
    private          boolean isPublic;

    private final ConcurrentHashMap<Long, Membership> memberships = new ConcurrentHashMap<>();

    public Project(int id, @NotNull String name, int state, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.isPublic = isPublic;
    }

    /* ----- ----- ----- */

    public void setMember(@NotNull Membership member) {
        memberships.put(member.getUser().getID(), member);
    }

    public void removeMember(long userTurtleID) {
        memberships.remove(userTurtleID);
    }

    public @Nullable Membership getMember(@NotNull User user) {
        return memberships.get(user.getID());
    }

    /* ----- ----- ----- */

    public int getID() {
        return id;
    }

    public @NotNull String getName() {
        return name;
    }

    public int getState() {
        return state;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Set<Membership> getMemberships() {
        return Set.copyOf(memberships.values());
    }

    /* ----- ----- ----- */

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
