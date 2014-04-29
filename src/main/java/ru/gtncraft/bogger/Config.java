package ru.gtncraft.bogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mongodb.connection.ServerAddress;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Config extends YamlConfiguration {

    public Config(final FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public List<ServerAddress> getHosts() {
        return getStringList("storage.hosts").stream().map(ServerAddress::new).collect(Collectors.toList());
    }

    public Collection<String> getWorlds() {
        return getStringList("worlds").stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}
