package com.ferozity.fzcore.database;

import com.ferozity.fzcore.FZcore;
import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DatabaseManager {

    private final FZcore plugin;
    private Connection connection;
    private String dbType;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public DatabaseManager(FZcore plugin) {
        this.plugin = plugin;
    }

    public void setupSQLite(String fileName) {
        this.dbType = "sqlite";
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        String url = "jdbc:sqlite:" + new File(dataFolder, fileName + ".db").getAbsolutePath();
        try {
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("SQLite connected: " + fileName);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to SQLite: " + e.getMessage());
        }
    }

    public void setupMySQL(String host, int port, String database, String username, String password) {
        this.dbType = "mysql";
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connectMySQL();
    }

    private void connectMySQL() {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("MySQL connected: " + database);
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
        }
    }

    public void reconnect() {
        if (dbType.equals("mysql")) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
                connectMySQL();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to reconnect: " + e.getMessage());
            }
        }
    }

    public void executeAsync(String query, Object... params) {
        CompletableFuture.runAsync(() -> execute(query, params));
    }

    public void execute(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Query failed: " + e.getMessage());
        }
    }

    public void queryAsync(String query, Consumer<ResultSet> callback, Object... params) {
        CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    callback.accept(rs);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Query failed: " + e.getMessage());
                callback.accept(null);
            }
        });
    }

    public void createTable(String tableName, String columns) {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        execute(query);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (dbType.equals("mysql") && (connection == null || connection.isClosed())) {
                connectMySQL();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to check connection: " + e.getMessage());
        }
        return connection;
    }
}