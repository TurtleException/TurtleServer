package de.eldritch.turtle.server.data;

import de.eldritch.turtle.server.Main;
import de.eldritch.turtle.server.entities.Group;
import de.eldritch.turtle.server.moderation.ticket.SupportTicket;
import de.eldritch.turtle.server.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

// NOTE: some of these statements are NOT recommended if you have a large amount of users / groups / tickets / ...
@Deprecated
public class Statement<T> {
    // NOTE: Do NOT MODIFY these without also modifying the counterpart values in the EntityBuilder!
    // TODO: maybe introduce constants for the names?
    public static class Table {
        public static final Statement<Void> CREATE_TABLE_USERS                        = new Statement<>("CREATE TABLE IF NOT EXISTS `users` ( `turtle_id` bigint(20) NOT NULL, `discord_primary` bigint(20) DEFAULT NULL, `minecraft_primary` text DEFAULT NULL, PRIMARY KEY (`turtle_id`))");
        public static final Statement<Void> CREATE_TABLE_USER_SECONDARY_DISCORD       = new Statement<>("CREATE TABLE IF NOT EXISTS `user_secondary_discord` ( `user_turtle_id` bigint(20) NOT NULL, `discord` bigint(20) NOT NULL, PRIMARY KEY (`discord`))");
        public static final Statement<Void> CREATE_TABLE_USER_SECONDARY_MINECRAFT     = new Statement<>("CREATE TABLE IF NOT EXISTS `user_secondary_minecraft` ( `user_turtle_id` bigint(20) NOT NULL, `minecraft` varchar(36) NOT NULL, PRIMARY KEY (`minecraft`))");
        public static final Statement<Void> CREATE_TABLE_GROUPS                       = new Statement<>("CREATE TABLE IF NOT EXISTS `groups` ( `turtle_id` bigint(20) NOT NULL, `name` text NOT NULL, `permission_level` int(11) NOT NULL, PRIMARY KEY (`turtle_id`))");
        public static final Statement<Void> CREATE_TABLE_USER_GROUPS                  = new Statement<>("CREATE TABLE IF NOT EXISTS `user_groups` ( `user_turtle_id` bigint(20) NOT NULL, `group_turtle_id` bigint(20) NOT NULL, PRIMARY KEY (`user_turtle_id`,`group_turtle_id`))");
        public static final Statement<Void> CREATE_TABLE_TICKET_SUPPORT               = new Statement<>("CREATE TABLE IF NOT EXISTS `ticket_support` ( `turtle_id` bigint(20) NOT NULL, `user_turtle_id` bigint(20) NOT NULL, `state` int(11) NOT NULL, `discord_channel_id` bigint(20) NOT NULL, PRIMARY KEY (`turtle_id`))");
        public static final Statement<Void> CREATE_TABLE_TICKET_SUPPORT_JOINED_GROUPS = new Statement<>("CREATE TABLE IF NOT EXISTS `ticket_support_joined_users` ( `ticket_turtle_id` bigint(20) NOT NULL, `user_turtle_id` bigint(20) NOT NULL, PRIMARY KEY (`ticket_turtle_id`,`user_turtle_id`))");
    }

    public static class Get {
        public static final Statement<List<User>>          SELECT_USERS             = new Statement<>("SELECT * FROM `users`",                                           resultSet -> /* TODO */ null);
        public static final Statement<User>                SELECT_USER_BY_ID        = new Statement<>("SELECT * FROM `users` WHERE `turtle_id` = '{0}' LIMIT 1",         resultSet -> /* TODO */ null);
        public static final Statement<User>                SELECT_USER_BY_DISCORD   = new Statement<>("SELECT * FROM `users` WHERE `discord_primary` = '{0}' LIMIT 1",   resultSet -> /* TODO */ null);
        public static final Statement<User>                SELECT_USER_BY_MINECRAFT = new Statement<>("SELECT * FROM `users` WHERE `minecraft_primary` = '{0}' LIMIT 1", resultSet -> /* TODO */ null);
        public static final Statement<List<Group>>         SELECT_GROUPS               = new Statement<>("SELECT * FROM `groups`",                                   resultSet -> /* TODO */ null);
        public static final Statement<Group>               SELECT_GROUP_BY_ID          = new Statement<>("SELECT * FROM `groups` WHERE `turtle_id` = '{0}'",         resultSet -> /* TODO */ null);
        public static final Statement<List<SupportTicket>> SELECT_OPEN_SUPPORT_TICKETS = new Statement<>("SELECT * FROM `ticket_support` WHERE `state` <> 0",        resultSet -> /* TODO */ null);
        public static final Statement<SupportTicket>       SELECT_SUPPORT_TICKET_BY_ID = new Statement<>("SELECT * FROM `ticket_support` WHERE `turtle_id` = '{0}'", resultSet -> /* TODO */ null);
    }

    public static class Set {
        /**
         * Inserts a user into the <code>users</code> table. If a user with the same primary key (the same turtle id)
         * already exist it is updated.
         */
        public static final Statement<Void> INSERT_USER = new Statement<>("INSERT INTO `users` (`turtle_id`, `discord_primary`, `minecraft_primary`) VALUES ('{0}', '{1}', '{2}') ON DUPLICATE KEY UPDATE `discord_primary`='{1}', `minecraft_primary`='{2}'");
        /**
         * Inserts a group into the <code>groups</code> table. If a group with the same primary key (the same turtle id)
         * already exist it is updated.
         */
        public static final Statement<Void> INSERT_GROUP = new Statement<>("INSERT INTO `groups` (`turtle_id`, `name`, `permission_level`) VALUES ('{0}', '{1}', '{2}') ON DUPLICATE KEY UPDATE `name`='{1}', `permission_level`='{2}'");
        /**
         * Inserts a support ticket into the <code>ticket_support</code> table. If a support ticket with the same
         * primary key (the same turtle id) already exist it is updated.
         */
        public static final Statement<Void> INSERT_SUPPORT_TICKET = new Statement<>("INSERT INTO `ticket_support` (`turtle_id`, `user_turtle_id`, `state`, `discord_channel_id`) VALUES ('{0}', '{1}', '{2}', '{3}') ON DUPLICATE KEY UPDATE `user_turtle_id`='{1}', `state`='{2}', `discord_channel_id`='{3}'");
    }

    public static class Edit {
        public static final Statement<Void> UPDATE_USER_DISCORD_PRIMARY   = new Statement<>("UPDATE `users` SET `discord_primary` = '{0}' WHERE `users`.`turtle_id` = '{1}'");
        public static final Statement<Void> UPDATE_USER_MINECRAFT_PRIMARY = new Statement<>("UPDATE `users` SET `minecraft_primary` = '{0}' WHERE `users`.`turtle_id` = '{2}'");
    }

    public static class Delete {
        public static final Statement<Void> DELETE_USER  = new Statement<>("DELETE FROM `users` WHERE `users`.`turtle_id` = '{0}'");
        public static final Statement<Void> DELETE_GROUP = new Statement<>("DELETE FROM `groups` WHERE `groups`.`turtle_id` = '{0}'");
        public static final Statement<Void> DELETE_USER_GROUPS_RELATION = new Statement<>("DELETE FROM `user_groups` WHERE `user_groups`.`user_turtle_id` = '{0}' AND `user_groups`.`group_turtle_id` = '{1}'");
        public static final Statement<Void> DELETE_USER_GROUPS_BY_USER  = new Statement<>("DELETE FROM `user_groups` WHERE `user_groups`.`user_turtle_id` = '{0}'");
        public static final Statement<Void> DELETE_USER_GROUPS_BY_GROUP = new Statement<>("DELETE FROM `user_groups` WHERE `user_groups`.`group_turtle_id` = '{0}'");
    }


    /* ----- ----- ----- */


    @NotNull
    final String statement;
    @NotNull
    private final Function<ResultSet, T> resultFunction;

    private Object[] args;

    @NotNull
    Consumer<Throwable> failConsumer = (throwable -> {});

    public Statement(@NotNull String sql, @NotNull Function<ResultSet, T> resultFunction) {
        this.statement = sql;
        this.resultFunction = resultFunction;
    }

    public Statement(@NotNull String sql) {
        this(sql, resultSet -> null);
    }

    public Statement<T> withArgs(@Nullable Object... args) {
        Statement<T> newStatement = new Statement<>(statement, resultFunction);
        newStatement.failConsumer = this.failConsumer;
        newStatement.args = args;
        return newStatement;
    }

    public Statement<T> withResultFunction(@NotNull Function<ResultSet, T> resultFunction) {
        Statement<T> newStatement = new Statement<>(statement, resultFunction);
        newStatement.failConsumer = this.failConsumer;
        newStatement.args = this.args;
        return newStatement;
    }

    public Statement<T> withFailConsumer(@NotNull Consumer<Throwable> failConsumer) {
        Statement<T> newStatement = new Statement<>(statement, resultFunction);
        newStatement.failConsumer = failConsumer;
        newStatement.args = this.args;
        return newStatement;
    }

    public void execute() {
        Main.singleton.getDataService().execute(this, args);
    }

    public T executeQuery() {
        return resultFunction.apply(Main.singleton.getDataService().executeQuery(this, args));
    }
}
