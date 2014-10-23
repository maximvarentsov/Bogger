package ru.gtncraft.bogger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.FindOptions;
import org.bson.Document;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.bukkit.Location;
import ru.gtncraft.bogger.codecs.LocationCodecProvider;
import ru.gtncraft.bogger.codecs.LogEntryCodecProvider;

import java.util.*;

class Storage implements AutoCloseable {
    private final MongoClient client;
    private final MongoDatabase db;
    private final FindOptions findOptions;

    public Storage(final Bogger plugin) throws Exception {
        List<CodecProvider> codecs = Arrays.asList(new LocationCodecProvider(), new LogEntryCodecProvider());
        MongoDatabaseOptions options = MongoDatabaseOptions.builder().codecRegistry(new RootCodecRegistry(codecs)).build();

        findOptions = new FindOptions().limit(plugin.getConfig().getInt("results")).sort(new Document("_id", -1));

        List<ServerAddress> hosts = new ArrayList<ServerAddress>();
        for (String host : plugin.getConfig().getStringList("mongodb.hosts")) {
            hosts.add(new ServerAddress(host));
        }

        client = new MongoClient(hosts,
            MongoClientOptions.builder().sslEnabled(plugin.getConfig().getBoolean("mongodb.ssl")).build()
        );

        db = client.getDatabase(plugin.getConfig().getString("mongodb.name"), options);

        for (String world : plugin.getConfig().getStringList("worlds")) {
            getCollection(world).createIndex(new Document("x", 1).append("y", 1).append("z", 1));
        }
    }

    public void insert(String world, List<LogEntry> documents) {
        getCollection(world).insertMany(documents);
    }

    public MongoCollection<LogEntry> getCollection(String name) {
        return db.getCollection(name, LogEntry.class);
    }

    public Collection<LogEntry> find(Location location) {
        String world = location.getWorld().getName();
        return getCollection(world).find(location, findOptions).into(new LinkedList<LogEntry>());
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
