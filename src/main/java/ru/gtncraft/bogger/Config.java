package ru.gtncraft.bogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;

public class Config extends YamlConfiguration {

    public Config(final FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public Collection<String> getHosts() {
        return getStringList("mongodb.hosts");
    }

    public boolean getSSL() {
        return getBoolean("mongodb.ssl");
    }

    public String getDatabase() {
        return getString("mongodb.name");
    }

    public Collection<String> getWorlds() {
        return getStringList("worlds");
    }
}
