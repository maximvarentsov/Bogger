package ru.gtncraft.bogger;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;

public final class Bogger extends JavaPlugin {

    private Storage storage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            storage = new Storage(this);
            new Listeners(this, storage);
        } catch (IOException ex) {
            setEnabled(false);
            getLogger().severe(ex.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            storage.close();
        } catch (Exception ex) {
            getLogger().severe(ex.getMessage());
        }
    }
}
