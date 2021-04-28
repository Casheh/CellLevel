package net.casheh.celllevel.db;

import net.casheh.celllevel.CellLevel;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite extends Database {

    private String islandTable = "CREATE TABLE IF NOT EXISTS virtual (" +
            "islandId VARCHAR PRIMARY KEY," +
            "beacons INTEGER," +
            "sponge INTEGER" +
            ");";

    private String playerTable = "CREATE TABLE IF NOT EXISTS players (" +
            "uuid VARCHAR PRIMARY KEY," +
            "beacons INTEGER," +
            "sponge INTEGER" +
            ");";


    public SQLite(String prefix) {
        super(prefix);
    }

    @Override
    boolean initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Connection open() {
        if (initialize()) {
            File data = new File(CellLevel.inst.getDataFolder(), "data.db");
            if (!data.exists())
                try {
                    data.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            try {
                if (connection != null && !connection.isClosed())
                    return connection;
                connection = DriverManager.getConnection("jdbc:sqlite:" + data);
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        if (connection == null)
            return open();
        try {
            if (connection.isClosed())
                return open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public boolean checkConnection() {
        if (connection != null)
            return true;
        return false;
    }

    @Override
    public ResultSet query(String query) {
        ResultSet rs = null;
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            switch (getStatement(query)) {
                case SELECT:
                    rs = ps.executeQuery();
                    break;
                default:
                    ps.executeUpdate();
            }
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PreparedStatement prepare(String query) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
            return ps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean createTable() {
        Statement s = null;
        try {
            if (this.islandTable == null || this.islandTable.isEmpty() || this.playerTable == null || this.playerTable.isEmpty())
                return false;
            s = connection.createStatement();
            s.execute(this.islandTable);
            s.execute(this.playerTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean checkTable(String table) {
        try {
            Statement s = getConnection().createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + table);
            if (rs == null)
                return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // This method has been altered to also create a new table,
    // acting like a tale reset for the cell level
    @Override
    public boolean wipeTable(String table) {
        Statement s = null;
        String query = null;
        try {
            if (!this.checkTable(table))
                return false;
            s = getConnection().createStatement();
            query = "DELETE FROM " + table;
            s.executeUpdate(query);
            createTable();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getCreateStatement(String table) {
        return null;
    }
}

