package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;

public final class Bogger extends JavaPlugin {
    private LogManager logManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(getConfig().getString("dateFormat", "dd.MM.yyyy HH:mm:ss"));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            logManager = new LogManager(getConfig().getString("mongodb.host"), getConfig().getString("mongodb.name"));
        } catch (Exception ex) {
            setEnabled(false);
            getLogger().severe(ex.getMessage());
        }

        LogWorld.limit = getConfig().getInt("results", 10);

        for (String world : getConfig().getStringList("worlds")) {
            logManager.register(world);
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                for (LogWorld log : logManager) {
                    log.save();
                }
            }
        }, 120L, 40L);

        new Listeners(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            logManager.close();
        } catch (Exception ignore) {
        }
    }

    public void blockBreak(Block block, Player player) {
        logManager.add(block, player, Log.Action.BREAK);
    }

    public void blockPaste(Block block, Player player) {
        logManager.add(block, player, Log.Action.PLACE);
    }

    public String[] history(Location location) {
        Collection<String> result = new LinkedList<String>();
        for (Log state : logManager.find(location)) {
            String message = ChatColor.DARK_AQUA + dateFormat.format(state.id.getDate()) + " " +
            Bukkit.getOfflinePlayer(state.uuid).getName() + " " + state.block + " " + state.action.name();
            result.add(message);
        }
        return result.toArray(new String[LogWorld.limit]);
    }
}
