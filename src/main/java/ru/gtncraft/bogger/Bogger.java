package ru.gtncraft.bogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class Bogger extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            new Listeners(this);
        } catch (Exception ex) {
            setEnabled(false);
            getLogger().severe(ex.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
}
