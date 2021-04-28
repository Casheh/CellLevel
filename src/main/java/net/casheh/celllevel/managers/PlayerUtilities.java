package net.casheh.celllevel.managers;

import net.casheh.celllevel.CellLevel;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerUtilities {

    private UUID uuid;

    public PlayerUtilities(UUID uuid) {
        this.uuid = uuid;
    }

    public int getBeacons() {
        try {

            String query = "SELECT * FROM players WHERE uuid=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt("beacons");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSponge() {
        try {

            String query = "SELECT * FROM players WHERE uuid=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt("sponge");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setBeacons(int amount) {
        try {

            String query = "SELECT * FROM players WHERE uuid=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                query = "UPDATE players SET beacons=? WHERE uuid=?";
                PreparedStatement update = CellLevel.inst.getDatabase().prepare(query);
                update.setInt(1, amount);
                update.setString(2, uuid.toString());
                update.executeUpdate();

            } else {

                query = "INSERT INTO players (uuid,beacons,sponge) VALUES (?,?,?)";
                PreparedStatement insert = CellLevel.inst.getDatabase().prepare(query);
                insert.setString(1, uuid.toString());
                insert.setInt(2, amount);
                insert.setInt(3, 0);
                insert.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSponge(int amount) {
        try {

            String query = "SELECT * FROM players WHERE uuid=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                query = "UPDATE players SET sponge=? WHERE uuid=?";
                PreparedStatement update = CellLevel.inst.getDatabase().prepare(query);
                update.setInt(1, amount);
                update.setString(2, uuid.toString());
                update.executeUpdate();

            } else {

                query = "INSERT INTO players (uuid,beacons,sponge) VALUES (?,?,?)";
                PreparedStatement insert = CellLevel.inst.getDatabase().prepare(query);
                insert.setString(1, uuid.toString());
                insert.setInt(2, 0);
                insert.setInt(3, amount);
                insert.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
