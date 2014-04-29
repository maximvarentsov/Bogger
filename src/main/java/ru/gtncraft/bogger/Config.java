package ru.gtncraft.bogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mongodb.connection.ServerAddress;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config extends YamlConfiguration {

    public Config(final FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public Stream<ServerAddress> getHosts() {
        return getStringList("storage.hosts").stream().map(ServerAddress::new);
    }

    public Stream<String> getWorlds() {
        return getStringList("worlds").stream().map(String::toLowerCase);
    }
}
