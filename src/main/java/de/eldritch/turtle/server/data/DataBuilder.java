package de.eldritch.turtle.server.data;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.TurtleServer;
import de.eldritch.turtle.server.entities.Group;
import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import de.eldritch.turtle.server.project.Membership;
import de.eldritch.turtle.server.project.Project;
import de.eldritch.turtle.server.user.User;
import de.eldritch.turtle.server.user.UserBuilder;
import de.eldritch.turtle.server.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

// TODO
class DataBuilder {
    public static final NestedToggleLogger LOGGER = new NestedToggleLogger("DataBuilder", TurtleServer.LOGGER);

    /**
     * Build a {@link User} object from a {@link ResultSet} that resulted from a call of {@link Statements#GET_USER}.
     * This method will fail silently and return <code>null</code> if the user could not be parsed.
     * <p><b>NOTE</b>: All relations that lie outside the <code>users</code> table must be added separately as they are
     * out of scope for this method.
     */
    public static @Nullable User buildUser(@NotNull ResultSet resultSet) {
        UserBuilder builder = new UserBuilder();

        try {
            if (!resultSet.next()) {
                LOGGER.log(Level.WARNING, "Could not parse user from empty ResultSet.");
                return null;
            }

            builder.setTurtle(resultSet.getLong(Identifiers.T_USERS_C_TURTLE_ID));
            builder.setDiscordPrimary(resultSet.getLong(Identifiers.T_USERS_C_DISCORD_PRIMARY));

            String uuidStr = resultSet.getString(Identifiers.T_USERS_C_MINECRAFT_PRIMARY);
            try {
                builder.setMinecraftPrimary(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.FINER, "Could not parse UUID '" + uuidStr + "'.", e);
            }

            return builder.build();
        } catch (SQLException | IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Could not parse user from ResultSet.", e);
            return null;
        }
    }

    public static @Nullable Group buildGroup(@NotNull ResultSet resultSet) {

    }

    public static @Nullable Project buildProject(@NotNull ResultSet resultRawProject, @NotNull ResultSet resultUsers) {
        Project project = null;

        try {
            if (!resultRawProject.next()) {
                LOGGER.log(Level.WARNING, "Could not parse project from empty ResultSet.");
                return null;
            }

            project = new Project(
                    resultRawProject.getInt(Identifiers.T_PROJECTS_C_ID),
                    resultRawProject.getString(Identifiers.T_PROJECTS_C_NAME),
                    resultRawProject.getInt(Identifiers.T_PROJECT_USERS_C_STATE),
                    resultRawProject.getBoolean(Identifiers.T_PROJECTS_C_PUBLIC)
            );
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not parse project from ResultSet.", e);
        }

        if (project == null) return null;

        try {
            while (resultUsers.next()) {
                User user = Main.singleton.getDataService().getUser(resultUsers.getLong(Identifiers.T_PROJECT_USERS_C_USER_TURTLE_ID));

                if (user == null) continue;

                project.setMember(new Membership(
                        project, user,
                        resultUsers.getInt(Identifiers.T_PROJECT_USERS_C_STATE),
                        resultUsers.getString(Identifiers.T_PROJECT_USERS_C_DATA)
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not parse project memberships for project " + project.getID() + " from ResultSet.", e);
        }

        return project;
    }

    public static @Nullable SupportTicket buildSupportTicket(@NotNull ResultSet resultSet) {

    }

    /* --- OTHER UTILITIES --- */

    public static @Nullable List<Long> parseLongsFromSingleColumn(@NotNull ResultSet resultSet) {
        try {
            ArrayList<Long> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getLong(1));
            }
            return list;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not parse Longs from ResultSet.", e);
            return null;
        }
    }

    public static @Nullable List<UUID> parseUUIDsFromSingleColumn(@NotNull ResultSet resultSet) {
        try {
            ArrayList<UUID> list = new ArrayList<>();
            while (resultSet.next()) {
                String uuidStr = resultSet.getString(1);
                try {
                    list.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.FINER, "Could not parse UUID '" + uuidStr + "'.", e);
                }
            }
            return list;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not parse UUIDs from ResultSet.", e);
            return null;
        }
    }
}
