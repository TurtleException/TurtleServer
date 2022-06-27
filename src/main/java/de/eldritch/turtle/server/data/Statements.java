package de.eldritch.turtle.server.data;

class Statements {
    public static final String CREATE_TABLE_USERS                       = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_USERS + "` ( `" + Identifiers.T_USERS_C_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_USERS_C_DISCORD_PRIMARY + "` bigint(20) DEFAULT NULL, `" + Identifiers.T_USERS_C_MINECRAFT_PRIMARY + "` text DEFAULT NULL, PRIMARY KEY (`" + Identifiers.T_USERS_C_TURTLE_ID + "`))";
    public static final String CREATE_TABLE_USER_SECONDARY_DISCORD      = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_USER_SECONDARY_DISCORD + "` ( `" + Identifiers.T_USER_SECONDARY_DISCORD_C_USER_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_USER_SECONDARY_DISCORD_C_DISCORD + "` bigint(20) NOT NULL, PRIMARY KEY (`" + Identifiers.T_USER_SECONDARY_DISCORD_C_DISCORD + "`))";
    public static final String CREATE_TABLE_USER_SECONDARY_MINECRAFT    = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_USER_SECONDARY_MINECRAFT + "` ( `" + Identifiers.T_USER_SECONDARY_MINECRAFT_C_USER_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_USER_SECONDARY_MINECRAFT_C_MINECRAFT + "` varchar(36) NOT NULL, PRIMARY KEY (`" + Identifiers.T_USER_SECONDARY_MINECRAFT_C_MINECRAFT + "`))";
    public static final String CREATE_TABLE_GROUPS                      = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_GROUPS + "` ( `" + Identifiers.T_GROUPS_C_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_GROUPS_C_NAME + "` text NOT NULL, `" + Identifiers.T_GROUPS_C_PERMISSION_LEVEL + "` int(11) NOT NULL, PRIMARY KEY (`" + Identifiers.T_GROUPS_C_TURTLE_ID + "`))";
    public static final String CREATE_TABLE_USER_GROUPS                 = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_USER_GROUPS + "` ( `" + Identifiers.T_USER_GROUPS_C_USER_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_USER_GROUPS_C_GROUP_TURTLE_ID + "` bigint(20) NOT NULL, PRIMARY KEY (`" + Identifiers.T_USER_GROUPS_C_USER_TURTLE_ID + "`,`" + Identifiers.T_USER_GROUPS_C_GROUP_TURTLE_ID + "`))";
    public static final String CREATE_TABLE_TICKET_SUPPORT              = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_TICKET_SUPPORT + "` ( `" + Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_TICKET_SUPPORT_C_USER_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_TICKET_SUPPORT_C_STATE + "` int(11) NOT NULL, `" + Identifiers.T_TICKET_SUPPORT_C_DISCORD_CHANNEL_ID + "` bigint(20) NOT NULL, PRIMARY KEY (`" + Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID + "`))";
    public static final String CREATE_TABLE_TICKET_SUPPORT_JOINED_USERS = "CREATE TABLE IF NOT EXISTS `" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS + "` ( `" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS_C_TICKET_TURTLE_ID + "` bigint(20) NOT NULL, `" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS_C_USER_TURTLE_ID + "` bigint(20) NOT NULL, PRIMARY KEY (`" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS_C_TICKET_TURTLE_ID + "`,`" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS_C_USER_TURTLE_ID + "`))";

    public static final String GET_USER_IDS           = "SELECT `" + Identifiers.T_USERS_C_TURTLE_ID + "` FROM `" + Identifiers.T_USERS + "`";
    public static final String GET_USER               = "SELECT * FROM `" + Identifiers.T_USERS + "` WHERE `" + Identifiers.T_USERS_C_TURTLE_ID + "` = '{0}'";
    public static final String GET_USER_GROUPS        = "SELECT * FROM `" + Identifiers.T_USER_GROUPS + "` WHERE `" + Identifiers.T_USER_GROUPS_C_USER_TURTLE_ID + "` = '{0}'";
    public static final String GET_USER_SEC_DISCORD   = "SELECT * FROM `" + Identifiers.T_USER_SECONDARY_DISCORD + "` WHERE `" + Identifiers.T_USER_SECONDARY_DISCORD_C_USER_TURTLE_ID + "` = '{0}'";
    public static final String GET_USER_SEC_MINECRAFT = "SELECT * FROM `" + Identifiers.T_USER_SECONDARY_MINECRAFT + "` WHERE `" + Identifiers.T_USER_SECONDARY_MINECRAFT_C_USER_TURTLE_ID + "` = '{0}'";

    public static final String GET_GROUP_IDS = "SELECT `" + Identifiers.T_GROUPS_C_TURTLE_ID + "` FROM `" + Identifiers.T_GROUPS + "`";
    public static final String GET_GROUP     = "SELECT * FROM `" + Identifiers.T_GROUPS + "` WHERE `" + Identifiers.T_GROUPS_C_TURTLE_ID + "` = '{0}'";

    public static final String GET_PROJECT_IDS = "SELECT `" + Identifiers.T_PROJECTS_C_ID + "` FROM `" + Identifiers.T_PROJECTS + "`";
    public static final String GET_PROJECT = "SELECT * FROM `" + Identifiers.T_PROJECTS + "` WHERE `" + Identifiers.T_PROJECTS_C_ID + "` = '{0}'";

    public static final String GET_PROJECT_USERS = "SELECT * FROM `" + Identifiers.T_PROJECT_USERS + "` WHERE `" + Identifiers.T_PROJECT_USERS_C_PROJECT_ID + "` = '{0}'";

    public static final String GET_TICKET_SUPPORT_IDS = "SELECT `" + Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID + "` FROM `" + Identifiers.T_TICKET_SUPPORT + "`";
    public static final String GET_TICKET_SUPPORT = "SELECT * FROM `" + Identifiers.T_TICKET_SUPPORT + "` WHERE `" + Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID + "` = '{0}'";
    public static final String GET_TICKET_SUPPORT_JOINED_USERS = "SELECT `" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS_C_USER_TURTLE_ID + "` FROM `" + Identifiers.T_TICKET_SUPPORT_JOINED_USERS + "` WHERE `" + Identifiers.T_TICKET_SUPPORT_C_TURTLE_ID + "` = '{0}'";
}
