package de.eldritch.turtle.server.data;

import org.jetbrains.annotations.Nullable;

import java.sql.*;

// TODO: docs
class DatabaseConnection {
    private final String url;
    private final String user;
    private final String pass;

    private Connection connection;

    public DatabaseConnection(String server, int port, String database, String user, String password) throws SQLException {
        this.url = "jdbc:mysql://" + server + ":" + port + "/" + database;
        this.user = user;
        this.pass = password;

        this.checkConnection();
    }

    public void shutdown() throws SQLException {
        connection.close();
    }

    /* ----- ----- ----- */

    private void checkConnection() throws SQLException {
        if (connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                // TODO: handle failed reconnect
            }
        }
    }

    /* ----- ----- ----- */

    public void execute(String sql) throws SQLException {
        connection.prepareStatement(sql).execute();
    }

    public void executeWithSilentFail(String sql) {
        try {
            this.execute(sql);
        } catch (SQLException ignored) { }
    }

    public @Nullable ResultSet executeQuery(String sql) throws SQLException {
        return connection.prepareStatement(sql).executeQuery();
    }

    public @Nullable ResultSet executeQueryWithSilentFail(String sql) {
        try {
            return this.executeQuery(sql);
        } catch (SQLException ignored) {
            return null;
        }
    }
}
