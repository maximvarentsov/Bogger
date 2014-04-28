package ru.gtncraft.bogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class Bogger extends JavaPlugin {

    Storage storage;
    BlockQueue queue;
    Config config;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        config = new Config(super.getConfig());
        queue = new BlockQueue(getConfig().getWorlds());
        new Listeners(this);

        try {
            storage = new Storage(this);
        } catch (Exception ex) {
            setEnabled(false);
            getLogger().severe(ex.getMessage());
            return;
        }

        // Flush queue every 40 tick.
        getServer().getScheduler().runTaskTimerAsynchronously(this, () ->
            getQueue().flush().entrySet().forEach(entry -> getStorage().insert(entry.getKey(), entry.getValue())),
        0L, 40L);
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

    @Override
    public Config getConfig() {
        return config;
    }

    public Storage getStorage() {
        return storage;
    }

    public BlockQueue getQueue() {
        return queue;
    }
}
