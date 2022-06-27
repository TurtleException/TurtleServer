package de.eldritch.turtle.server.data;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.entities.Group;
import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import de.eldritch.turtle.server.project.Project;
import de.eldritch.turtle.server.user.User;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DataService {
    private DatabaseConnection connection;

    private final NestedToggleLogger logger = new NestedToggleLogger("DataService" , TurtleServer.LOGGER);

    public DataService() throws NullPointerException, SQLException {
        this.init();
    }

    public void init() throws NullPointerException, SQLException {
        ConfigurationSection sqlConfig = Main.singleton.getConfig().getConfigurationSection("sql");

        if (sqlConfig == null)
            throw new NullPointerException("ConfigurationSection 'sql' does not exist.");

        this.connection = new DatabaseConnection(
                sqlConfig.getString("server"),
                sqlConfig.getInt("port"),
                sqlConfig.getString("database"),
                sqlConfig.getString("user"),
                sqlConfig.getString("pass")
        );

        this.createTables();
    }

    public void shutdown() {
        try {
            this.connection.shutdown();
        } catch (SQLException ignored) { }
    }

    /* ----- ----- ----- */

    public void createTables() {
        connection.executeWithSilentFail(Statements.CREATE_TABLE_USERS);
        connection.executeWithSilentFail(Statements.CREATE_TABLE_USER_SECONDARY_DISCORD);
        connection.executeWithSilentFail(Statements.CREATE_TABLE_USER_SECONDARY_MINECRAFT);

        connection.executeWithSilentFail(Statements.CREATE_TABLE_GROUPS);
        connection.executeWithSilentFail(Statements.CREATE_TABLE_USER_GROUPS);

        connection.executeWithSilentFail(Statements.CREATE_TABLE_TICKET_SUPPORT);
        connection.executeWithSilentFail(Statements.CREATE_TABLE_TICKET_SUPPORT_JOINED_USERS);
    }

    /* --- GET --- */

    /**
     * Provides the entire content of the table <code>users</code> and all relations to other tables. Each entry will be
     * parsed to a {@link User} object by the {@link DataBuilder}.
     * <p>This method returns null if the request to the database fails (silently). If the operation is successful but
     * does not provide any entries an empty list will be returned.
     */
    public @Nullable List<User> getUsers() {
        ResultSet result = connection.executeQueryWithSilentFail(Statements.GET_USER_IDS);

        // the request failed
        if (result == null) return null;

        try {
            ArrayList<User> users = new ArrayList<>();

            while (result.next()) {
                // if this throws an SQLException for one operation it will throw an exception for every operation
                users.add(this.getUser(result.getLong(Identifiers.T_USERS_C_TURTLE_ID)));
            }

            return List.copyOf(users);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not parse users from ResultSet.", e);
            return null;
        }
    }

    /**
     * Provides a single entry of the table <code>users</code> and all relation to other tables. The entry will be
     * parsed to a {@link User} object by the {@link DataBuilder}.
     * <p>This method returns null if no user with the specified ID exists in the database or the request to the
     * database fails (silently).
     */
    public @Nullable User getUser(long turtleID) {
        ResultSet resultRawUser      = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_USER, turtleID));
        ResultSet resultGroups       = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_USER_GROUPS, turtleID));
        ResultSet resultSecDiscord   = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_USER_SEC_DISCORD, turtleID));
        ResultSet resultSecMinecraft = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_USER_SEC_MINECRAFT, turtleID));

        // the request failed
        if (resultRawUser == null) return null;

        // relations might cause building the user to fail too to prevent false processing of a user
        if (resultGroups       == null) return null;
        if (resultSecDiscord   == null) return null;
        if (resultSecMinecraft == null) return null;

        List<Long> groupIDs     = DataBuilder.parseLongsFromSingleColumn(resultGroups);
        List<Long> secDiscord   = DataBuilder.parseLongsFromSingleColumn(resultSecDiscord);
        List<UUID> secMinecraft = DataBuilder.parseUUIDsFromSingleColumn(resultSecMinecraft);

        if (groupIDs     == null) return null;
        if (secDiscord   == null) return null;
        if (secMinecraft == null) return null;


        User user = DataBuilder.buildUser(resultRawUser);

        // parsing the user failed
        if (user == null) {
            logger.log(Level.WARNING, "Could not parse user " + turtleID + " from ResultSet.");
            return null;
        }


        for (Long groupID : groupIDs) {
            if (groupID == null) continue;
            Group group = Main.singleton.getUserService().getGroup(groupID);

            if (group == null) {
                logger.log(Level.WARNING, "Unable to parse group " + groupID + ".");
            } else {
                user.addGroup(group);
            }
        }

        for (Long aSecDiscord : secDiscord) {
            if (aSecDiscord == null) continue;
            user.addDiscordSecondary(aSecDiscord);
        }

        for (UUID aSecMinecraft : secMinecraft) {
            if (aSecMinecraft == null) continue;
            user.addMinecraftSecondary(aSecMinecraft);
        }

        return user;
    }

    /**
     * Provides the entire content of the table <code>groups</code>. Each entry will be parsed to a {@link Group} object
     * by the {@link DataBuilder}.
     * <p>This method returns null if the request to the database fails (silently). If the operation is successful but
     * does not provide any entries an empty list will be returned.
     */
    public @Nullable List<Group> getGroups() {
        ResultSet result = connection.executeQueryWithSilentFail(Statements.GET_GROUP_IDS);

        // the request failed
        if (result == null) return null;

        try {
            ArrayList<Group> groups = new ArrayList<>();

            while (result.next()) {
                // if this throws an SQLException for one operation it will throw an exception for every operation
                groups.add(this.getGroup(result.getLong(Identifiers.T_GROUPS_C_TURTLE_ID)));
            }

            return List.copyOf(groups);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not parse groups from ResultSet.", e);
            return null;
        }
    }

    /**
     * Provides a single entry of the table <code>groups</code>. The entry will be parsed to a {@link Group} object by
     * the {@link DataBuilder}.
     * <p>This method returns null if no group with the specified ID exists in the database or the request to the
     * database fails (silently).
     */
    public @Nullable Group getGroup(long turtleID) {
        ResultSet result = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_GROUP, turtleID));

        // the request failed
        if (result == null) return null;

        Group group = DataBuilder.buildGroup(result);

        // parsing the group failed
        if (group == null) {
            logger.log(Level.WARNING, "Could not parse group " + turtleID + " from ResultSet.");
            return null;
        }

        return group;
    }


    /**
     * Provides the entire content of the table <code>projects</code>. Each entry will be parsed to a {@link Project}
     * object by the {@link DataBuilder}.
     * <p>This method returns null if the request to the database fails (silently). If the operation is successful but
     * does not provide any entries an empty list will be returned.
     */
    public @Nullable List<Project> getProjects() {
        ResultSet result = connection.executeQueryWithSilentFail(Statements.GET_PROJECT_IDS);

        // the request failed
        if (result == null) return null;

        try {
            ArrayList<Project> projects = new ArrayList<>();

            while (result.next()) {
                // if this throws an SQLException for one operation it will throw an exception for every operation
                projects.add(this.getProject(result.getInt(Identifiers.T_PROJECTS_C_ID)));
            }

            return List.copyOf(projects);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not parse projects from ResultSet.", e);
            return null;
        }
    }

    /**
     * Provides a single entry of the table <code>projects</code> and all relation to other tables. The entry will be
     * parsed to a {@link Project} object by the {@link DataBuilder}.
     * <p>This method returns null if no project with the specified ID exists in the database or the request to the
     * database fails (silently).
     */
    public @Nullable Project getProject(int id) {
        ResultSet resultRawProject = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_PROJECT, id));
        ResultSet resultUsers      = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_PROJECT_USERS, id));

        // the request failed
        if (resultRawProject == null) return null;
        if (resultUsers      == null) return null;

        Project project = DataBuilder.buildProject(resultRawProject, resultUsers);

        // parsing the project failed
        if (project == null) {
            logger.log(Level.WARNING, "Could not parse project " + id + " from ResultSet.");
            return null;
        }

        return project;
    }

    /**
     * Provides the entire content of the table <code>ticket_support</code> and all relations to other tables. Each
     * entry will be parsed to a {@link SupportTicket} object by the {@link DataBuilder}.
     * <p>This method returns null if the request to the database fails (silently). If the operation is successful but
     * does not provide any entries an empty list will be returned.
     */
    public @Nullable List<SupportTicket> getSupportTickets() {
        ResultSet result = connection.executeQueryWithSilentFail(Statements.GET_TICKET_SUPPORT_IDS);

        // the request failed
        if (result == null) return null;

        try {
            ArrayList<SupportTicket> tickets = new ArrayList<>();

            while (result.next()) {
                // if this throws an SQLException for one operation it will throw an exception for every operation
                tickets.add(this.getSupportTicket(result.getLong(Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID)));
            }

            return List.copyOf(tickets);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not parse support tickets from ResultSet.", e);
            return null;
        }
    }

    /**
     * Provides a single entry of the table <code>ticket_support</code> and all relation to other tables. The entry will
     * be parsed to a {@link SupportTicket} object by the {@link DataBuilder}.
     * <p>This method returns null if no support ticket with the specified ID exists in the database or the request to
     * the database fails (silently).
     */
    public @Nullable SupportTicket getSupportTicket(long turtleID) {
        ResultSet resultRawTicket   = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_TICKET_SUPPORT, turtleID));
        ResultSet resultJoinedUsers = connection.executeQueryWithSilentFail(MessageFormat.format(Statements.GET_TICKET_SUPPORT_JOINED_USERS, turtleID));

        // the request failed
        if (resultRawTicket   == null) return null;
        if (resultJoinedUsers == null) return null;

        SupportTicket ticket = DataBuilder.buildSupportTicket(resultRawTicket);

        // parsing the ticket failed
        if (ticket == null) {
            logger.log(Level.WARNING, "Could not parse support ticket " + turtleID + " from ResultSet.");
            return null;
        }

        List<Long> joinedUserIDs = DataBuilder.parseLongsFromSingleColumn(resultJoinedUsers);

        if (joinedUserIDs == null) return null;

        for (Long joinedUserID : joinedUserIDs) {
            User joinedUser = Main.singleton.getUserService().getUser(joinedUserID);
            if (joinedUser == null) continue;
            ticket.addUser(joinedUser);
        }

        return ticket;
    }

    /* --- CREATE --- */

    /**
     * Creates an entry in the table <code>user_secondary_discord</code>.
     */
    public void createUserSecondaryDiscordRelation(long userID, long snowflake) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>user_secondary_minecraft</code>.
     */
    public void createUserSecondaryMinecraftRelation(long userID, @NotNull UUID uuid) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>user_groups</code>.
     */
    public void createUserGroupRelation(long userID, long groupID) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>ticket_support_joined_users</code>.
     */
    public void createSupportTicketUserRelation(long supportTicketID, long userID) {
        // TODO
    }

    /* --- SET --- */

    /**
     * Creates an entry in the table <code>users</code> or updates an existing entry.
     */
    public void updateUser(long turtleID, @Nullable Long discordPrimary, @Nullable UUID minecraftPrimary) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>users</code> or updates an existing entry.
     */
    public void updateUserDiscordPrimary(long turtleID, @Nullable Long discordPrimary) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>users</code> or updates an existing entry.
     */
    public void updateUserMinecraftPrimary(long turtleID, @Nullable UUID minecraftPrimary) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>groups</code> or updates an existing entry.
     */
    public void updateGroup(long turtleID, @NotNull String name, int permissionLevel) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>groups</code> or updates an existing entry.
     */
    public void updateGroupName(long turtleID, @NotNull String name) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>groups</code> or updates an existing entry.
     */
    public void updateGroupPermissionLevel(long turtleID, int permissionLevel) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>ticket_support</code> or updates an existing entry.
     */
    public void updateSupportTicket(long turtleID, long userID, int state, long channelID) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>ticket_support</code> or updates an existing entry.
     */
    public void updateSupportTicketUser(long turtleID, long userID) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>ticket_support</code> or updates an existing entry.
     */
    public void updateSupportTicketState(long turtleID, int state) {
        // TODO
    }

    /**
     * Creates an entry in the table <code>ticket_support</code> or updates an existing entry.
     */
    public void updateSupportTicketChannel(long turtleID, long channelID) {
        // TODO
    }

    /* --- DELETE --- */

    /**
     * Deletes an entry in the table <code>users</code>.
     * If no user with the specified ID exists the operation fails silently.
     */
    public void deleteUser(long turtleID) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>groups</code>.
     * If no group with the specified ID exists the operation fails silently.
     */
    public void deleteGroup(long turtleID) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>ticket_support</code>.
     * If no support ticket with the specified ID exists the operation fails silently.
     */
    public void deleteSupportTicket(long turtleID) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>user_secondary_discord</code>.
     * If no such entry exists the operation fails silently.
     */
    public void deleteUserSecondaryDiscordRelation(long userID, long snowflake) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>user_secondary_minecraft</code>.
     * If no such entry exists the operation fails silently.
     */
    public void deleteUserSecondaryMinecraftRelation(long userID, @NotNull UUID uuid) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>user_groups</code>.
     * If no such entry exists the operation fails silently.
     */
    public void deleteUserGroupRelation(long userID, long groupID) {
        // TODO
    }

    /**
     * Deletes an entry in the table <code>ticket_support_joined_users</code>.
     * If no such entry exists the operation fails silently.
     */
    public void deleteSupportTicketUserRelation(long supportTicketID, long userID) {
        // TODO
    }
}
