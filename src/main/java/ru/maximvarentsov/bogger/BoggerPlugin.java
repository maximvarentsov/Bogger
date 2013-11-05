package ru.maximvarentsov.bogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class BoggerPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            new BoggerListener(this);
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
