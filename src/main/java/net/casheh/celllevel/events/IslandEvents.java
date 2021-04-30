package net.casheh.celllevel.events;

import me.goodandevil.skyblock.api.event.island.IslandDeleteEvent;
import me.goodandevil.skyblock.api.event.island.IslandKickEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandLeaveEvent;
import net.casheh.celllevel.CellLevel;
import net.casheh.celllevel.managers.IslandUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class IslandEvents implements Listener {

    @EventHandler
    public void onLeave(PlayerIslandLeaveEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
            @Override
            public void run() {
                try {
                    String query = "DELETE FROM players WHERE uuid=?";
                    PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
                    statement.setString(1, uuid.toString());
                    statement.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @EventHandler
    public void onIslandDisband(IslandDeleteEvent e) {
        IslandUtilities islandUtilities = new IslandUtilities(e.getIsland());
        List<UUID> allMembers = islandUtilities.getAllMembers();

        Bukkit.getScheduler().runTaskAsynchronously(CellLevel.inst, new Runnable() {
            @Override
            public void run() {
                try {
                    String query = "DELETE FROM players WHERE uuid=?";
                    PreparedStatement statement = null;

                    for (UUID uuid : allMembers) {
                        statement = CellLevel.inst.getDatabase().prepare(query);
                        statement.setString(1, uuid.toString());
                        statement.executeUpdate();
                    }

                    query = "DELETE FROM virtual WHERE islandId=?";
                    statement = CellLevel.inst.getDatabase().prepare(query);
                    statement.setString(1, e.getIsland().getIslandUUID().toString());
                    statement.executeUpdate();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @EventHandler
    public void onKick(IslandKickEvent e) {
        OfflinePlayer player = e.getKicked();

        try {
            String query = "DELETE FROM players WHERE uuid=?";
            PreparedStatement statement = CellLevel.inst.getDatabase().prepare(query);
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }



}
