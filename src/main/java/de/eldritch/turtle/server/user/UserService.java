package de.eldritch.turtle.server.user;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.data.Statement;
import de.eldritch.turtle.server.entities.Group;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class UserService {
    /**
     * This is where all {@link Group Groups} are stored an indexed by turtle id.
     */
    private final ConcurrentHashMap<Long, Group> groupCache = new ConcurrentHashMap<>();
    /**
     * This is where all {@link User Users} are stored an indexed by turtle id.
     */
    private final ConcurrentHashMap<Long, User>  userCache  = new ConcurrentHashMap<>();

    private final NestedToggleLogger logger = new NestedToggleLogger("UserService", TurtleServer.LOGGER);

    public UserService() {
        this.reloadGroups();
        this.reloadUsers();
    }

    public void shutdown() {
        for (Group group : groupCache.values())
            saveGroup(group);

        for (User user : userCache.values())
            saveUser(user);
    }

    /* ----- ----- ----- */

    public void reloadGroups() {
        List<Group> groups = Main.singleton.getDataService().getGroups();

        if (groups != null) {
            // invalidate cache
            groupCache.clear();

            // feed group objects to cache
            for (Group group : groups)
                groupCache.put(group.getID(), group);
        } else {
            logger.log(Level.WARNING, "Could not reload groups!");
        }
    }

    public void reloadUsers() {
        List<User> users = Main.singleton.getDataService().getUsers();

        if (users != null) {
            // invalidate cache
            userCache.clear();

            // feed user objects to cache
            for (User user : users)
                userCache.put(user.getID(), user);
        } else {
            logger.log(Level.WARNING, "Could not reload users!");
        }
    }

    /**
     * Saves a specific {@link Group} to the backing database.
     */
    public void saveGroup(@NotNull Group group) {
        // TODO: save group to DB
    }

    /**
     * Saved a specific {@link User} to the backing database.
     */
    public void saveUser(@NotNull User user) {
        // TODO: save user to DB

        // TODO: begone thot
        Statement.Set.INSERT_USER.withArgs(
                user.getID(),
                user.getDiscordPrimary(),
                user.getMinecraftPrimary()
        ).execute();

        // TODO: insert secondary discord / minecraft and group / ticket references


    }

    /* ----- ----- ----- */

    public @Nullable User getUser(long turtleID) {

    }

    public @Nullable User updateUser(long turtleID) {

    }

    public @NotNull List<User> updateUsers() {

    }

    public @Nullable Group getGroup(long turtleID) {

    }

    /* ----- ----- ----- */

    /**
     * Registers a group to the backing database and puts it in the cache.
     * @param group The group object.
     */
    void register(@NotNull Group group) {
        // put group into cache
        groupCache.put(group.getID(), group);

        // put group into database
        saveGroup(group);
    }

    /**
     * Registers a user to the backing database and puts them in the cache.
     * @param user The user object.
     */
    void register(@NotNull User user) {
        // put user into cache
        userCache.put(user.getID(), user);

        // put user into database
        saveUser(user);
    }
}
