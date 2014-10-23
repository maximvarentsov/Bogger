package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bogger extends JavaPlugin {
    public LogManager logManager;

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
}
