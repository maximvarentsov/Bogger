package ru.gtncraft.bogger;

import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.FindOptions;
import com.mongodb.operation.Index;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.mongodb.Document;

import java.io.IOException;
import java.util.*;

class Storage implements AutoCloseable {
    private final MongoClient client;
    private final MongoDatabase db;
    private final FindOptions findOptions;

    public Storage(final Bogger plugin) throws IOException {
        FileConfiguration config = plugin.getConfig();

        findOptions = new FindOptions().limit(config.getInt("results")).sort(new Document("_id", -1));

        List<ServerAddress> hosts = new ArrayList<ServerAddress>();
        for (String host : config.getStringList("mongodb.hosts")) {
            hosts.add(new ServerAddress(host));
        }

        client = new MongoClient(hosts,
            MongoClientOptions.builder().sslEnabled(config.getBoolean("mongodb.ssl")).build()
        );

        db = client.getDatabase(config.getString("mongodb.name"));

        for (String world : config.getStringList("worlds")) {
            createIndexes(world);
        }
    }

    private void createIndexes(String world) {
        getCollection(world).tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("x").addKey("y").addKey("z").build()
        ));
    }

    public void insert(String world, List<Document> documents) {
        db.getCollection(world).insertMany(documents);
    }

    public MongoCollection getCollection(String world) {
        return db.getCollection(world);
    }

    public Collection<BlockState> find(final Location location) {
        Collection<BlockState> result = new LinkedList<BlockState>();
        findOptions.criteria(new Document(new HashMap<String, Object>() {{
            put("x", location.getBlockX());
            put("y", location.getBlockY());
            put("z", location.getBlockZ());
        }}));
        String world = location.getWorld().getName();
        MongoIterable it = getCollection(world).find(findOptions);
        for (Object o : it) {
            result.add(new BlockState((Document) o));
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
