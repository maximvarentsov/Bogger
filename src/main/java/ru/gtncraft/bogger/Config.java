package ru.gtncraft.bogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.stream.Stream;

public class Config extends YamlConfiguration {

    public Config(final FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public Stream<String> getHosts() {
        return getStringList("mongodb.hosts").stream();
    }

    public boolean getSSL() {
        return getBoolean("mongodb.ssl");
    }

    public String getDatabase() {
        return getString("mongodb.name");
    }

    public Stream<String> getWorlds() {
        return getStringList("worlds").stream().map(String::toLowerCase);
    }
}
