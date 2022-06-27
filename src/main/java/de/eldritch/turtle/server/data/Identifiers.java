package de.eldritch.turtle.server.data;

/**
 * These constants ensure that renaming a table / column is a matter of changing <b>one</b> value and to prevent a typo
 * from f*ing up the entire database.
 */
class Identifiers {
    public static final String T_GROUPS                    = "groups";
    public static final String T_GROUPS_C_TURTLE_ID        = "turtle_id";
    public static final String T_GROUPS_C_NAME             = "name";
    public static final String T_GROUPS_C_PERMISSION_LEVEL = "permission_level";

    public static final String T_PROJECTS          = "projects";
    public static final String T_PROJECTS_C_ID     = "id";
    public static final String T_PROJECTS_C_NAME   = "name";
    public static final String T_PROJECTS_C_STATE  = "state";
    public static final String T_PROJECTS_C_PUBLIC = "public";

    public static final String T_PROJECT_USERS                  = "project_users";
    public static final String T_PROJECT_USERS_C_PROJECT_ID     = "project_id";
    public static final String T_PROJECT_USERS_C_USER_TURTLE_ID = "user_turtle_id";
    public static final String T_PROJECT_USERS_C_STATE          = "state";
    public static final String T_PROJECT_USERS_C_DATA           = "data";

    public static final String T_TICKET_SUPPORT                      = "ticket_support";
    public static final String T_TICKET_SUPPORT_C_TURTLE_ID          = "turtle_id";
    public static final String T_TICKET_SUPPORT_C_USER_TURTLE_ID     = "user_turtle_id";
    public static final String T_TICKET_SUPPORT_C_STATE              = "state";
    public static final String T_TICKET_SUPPORT_C_DISCORD_CHANNEL_ID = "discord_channel_id";

    public static final String T_TICKET_SUPPORT_JOINED_USERS                    = "ticket_support_joined_users";
    public static final String T_TICKET_SUPPORT_JOINED_USERS_C_TICKET_TURTLE_ID = "ticket_turtle_id";
    public static final String T_TICKET_SUPPORT_JOINED_USERS_C_USER_TURTLE_ID   = "user_turtle_id";

    public static final String T_USERS                     = "users";
    public static final String T_USERS_C_TURTLE_ID         = "turtle_id";
    public static final String T_USERS_C_DISCORD_PRIMARY   = "discord_primary";
    public static final String T_USERS_C_MINECRAFT_PRIMARY = "minecraft_primary";

    public static final String T_USER_GROUPS                   = "user_groups";
    public static final String T_USER_GROUPS_C_USER_TURTLE_ID  = "user_turtle_id";
    public static final String T_USER_GROUPS_C_GROUP_TURTLE_ID = "group_turtle_id";

    public static final String T_USER_SECONDARY_DISCORD                  = "user_secondary_discord";
    public static final String T_USER_SECONDARY_DISCORD_C_USER_TURTLE_ID = "user_turtle_id";
    public static final String T_USER_SECONDARY_DISCORD_C_DISCORD        = "discord";

    public static final String T_USER_SECONDARY_MINECRAFT                  = "user_secondary_minecraft";
    public static final String T_USER_SECONDARY_MINECRAFT_C_USER_TURTLE_ID = "user_turtle_id";
    public static final String T_USER_SECONDARY_MINECRAFT_C_MINECRAFT      = "minecraft";
}
