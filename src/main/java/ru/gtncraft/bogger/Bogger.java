package ru.gtncraft.bogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class Bogger extends JavaPlugin {
    private Storage storage;
    private BlockQueue queue;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        queue = new BlockQueue(getConfig().getStringList("worlds"));
        new Listeners(this);

        try {
            storage = new Storage(this);
            getServer().getScheduler().runTaskTimerAsynchronously(this, () ->
                getQueue().flush().entrySet().forEach(entry -> getStorage().insert(entry.getKey(), entry.getValue())),
            0L, 40L);
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
