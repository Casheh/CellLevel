package net.casheh.celllevel.managers;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandRole;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IslandUtilities {

    private final Island island;

    public IslandUtilities(Island island) {
        this.island = island;
    }

    public List<UUID> getAllMembers() {
        Set<UUID> members = this.island.getPlayersWithRole(IslandRole.MEMBER);
        Set<UUID> operators = this.island.getPlayersWithRole(IslandRole.OPERATOR);
        Set<UUID> owners = this.island.getPlayersWithRole(IslandRole.OWNER);

        List<UUID> total = new ArrayList<>();

        total.addAll(members);
        total.addAll(operators);
        total.addAll(owners);

        return total;
    }

    public int getBeacons() {
        UUID islandId = this.island.getIslandUUID();
        try {

            String query = "SELECT beacons FROM virtual WHERE islandId=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, islandId.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt("beacons");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSponge() {
        UUID islandId = this.island.getIslandUUID();
        try {

            String query = "SELECT sponge FROM virtual WHERE islandId=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, islandId.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
                return rs.getInt("sponge");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setBeacons(int amount) {
        UUID islandId = this.island.getIslandUUID();
        try {

            String query = "SELECT * FROM virtual WHERE islandId=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, islandId.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                query = "UPDATE virtual SET beacons=? WHERE islandId=?";
                PreparedStatement update = CellLevel.inst.getDatabase().prepare(query);
                update.setInt(1, amount);
                update.setString(2, islandId.toString());
                update.executeUpdate();

            } else {

                query = "INSERT INTO virtual (islandId, beacons, sponge) VALUES (?,?,?)";
                PreparedStatement insert = CellLevel.inst.getDatabase().prepare(query);
                insert.setString(1, islandId.toString());
                insert.setInt(2, amount);
                insert.setInt(3, 0);
                insert.executeUpdate();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSponge(int amount) {
        UUID islandId = this.island.getIslandUUID();
        try {

            String query = "SELECT * FROM virtual WHERE islandId=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, islandId.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                query = "UPDATE virtual SET sponge=? WHERE islandId=?";
                PreparedStatement update = CellLevel.inst.getDatabase().prepare(query);
                update.setInt(1, amount);
                update.setString(2, islandId.toString());
                update.executeUpdate();

            } else {

                query = "INSERT INTO virtual (islandId, beacons, sponge) VALUES (?,?,?)";
                PreparedStatement insert = CellLevel.inst.getDatabase().prepare(query);
                insert.setString(1, islandId.toString());
                insert.setInt(2, 0);
                insert.setInt(3, amount);
                insert.executeUpdate();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLevelsList() {
        Set<UUID> owners = this.island.getPlayersWithRole(IslandRole.OWNER);
        Set<UUID> operators = this.island.getPlayersWithRole(IslandRole.OPERATOR);
        Set<UUID> members = this.island.getPlayersWithRole(IslandRole.MEMBER);
        int totalBeacons = 0;
        int totalSponge = 0;

        List<String> lore = new ArrayList<>();
        lore.add(" ");

        if (owners.size() > 0) {
            lore.add(Util.color("&8» &c&lOwner &8«"));
            for (UUID uuid : owners) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                PlayerUtilities playerUtilities = new PlayerUtilities(uuid);
                int beacons = playerUtilities.getBeacons();
                int sponge = playerUtilities.getSponge();
                totalBeacons += beacons;
                totalSponge += sponge;
                lore.add(Util.color("&c&l" + player.getName() + "&r&8: &a&l" +
                    Util.addCommas((int) (beacons * 0.5) + sponge) + " &r&8(&b&lB: &f" + Util.addCommas(beacons) + " &8/ &e&lS: &r&f" + Util.addCommas(sponge) + "&r&8)"));
            }
        }

        if (operators.size() > 0) {
            lore.add(" ");
            lore.add(Util.color("&8» &e&lOperators &8«"));
            for (UUID uuid : operators) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                PlayerUtilities playerUtilities = new PlayerUtilities(uuid);
                int beacons = playerUtilities.getBeacons();
                int sponge = playerUtilities.getSponge();
                totalBeacons += beacons;
                totalSponge += sponge;
                lore.add(Util.color("&e&l" + player.getName() + "&r&8: &a&l" +
                        Util.addCommas((int) (beacons * 0.5) + sponge) + " &r&8(&b&lB: &f" + Util.addCommas(beacons) + " &8/ &e&lS: &r&f" + Util.addCommas(sponge) + "&r&8)"));
            }
        }

        if (members.size() > 0) {
            lore.add(" ");
            lore.add(Util.color("&8» &d&lMembers &8«"));
            for (UUID uuid : members) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                PlayerUtilities playerUtilities = new PlayerUtilities(uuid);
                int beacons = playerUtilities.getBeacons();
                int sponge = playerUtilities.getSponge();
                totalBeacons += beacons;
                totalSponge += sponge;
                lore.add(Util.color("&d&l" + player.getName() + "&r&8: &a&l" +
                        Util.addCommas((int) (beacons * 0.5) + sponge) + " &r&8(&b&lB: &f" + Util.addCommas(beacons) + " &8/ &e&lS: &r&f" + Util.addCommas(sponge) + "&r&8)"));
            }
        }

        lore.add(" ");
        lore.add(Util.color("&b&lTotal Beacons&r&8: &b&l" + Util.addCommas(totalBeacons)));
        lore.add(Util.color("&e&lTotal Sponge&r&8: &e&l" + Util.addCommas(totalSponge)));
        lore.add(" ");
        lore.add(Util.color("&b&n&lTotal Cell Value:&r &b&l" + Util.addCommas(((int) (0.5 * totalBeacons) + totalSponge))));

        return lore;
    }
}

