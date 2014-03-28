package ru.gtncraft.bogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;
import java.util.stream.Collectors;

public class Config extends YamlConfiguration {

    public Config(final FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public Collection<String> getWorlds() {
        return getStringList("worlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
