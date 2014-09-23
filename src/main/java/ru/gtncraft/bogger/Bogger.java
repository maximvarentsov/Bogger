package ru.gtncraft.bogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class Bogger extends JavaPlugin {
    private Storage storage;
    private BlockQueue queue;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            storage = new Storage(this);
            queue = new BlockQueue(this);
            new Listeners(this);
        } catch (Exception ex) {
            setEnabled(false);
            getLogger().severe(ex.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            getStorage().close();
        } catch (Exception ex) {
            getLogger().severe(ex.getMessage());
        }
    }

    Storage getStorage() {
        return storage;
    }

    BlockQueue getQueue() {
        return queue;
    }
}
