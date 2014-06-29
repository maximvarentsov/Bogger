package ru.gtncraft.bogger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.mongodb.*;
import org.mongodb.connection.ServerAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class Storage implements AutoCloseable {

    final int maxResult;
    final MongoClient client;
    final MongoDatabase db;

    public Storage(final Bogger plugin) throws IOException {

        maxResult = plugin.getConfig().getInt("results");

        List<ServerAddress> hosts = new ArrayList<>();
        for (String host : plugin.getConfig().getHosts()) {
            hosts.add(new ServerAddress(host));
        }

        client = MongoClients.create(
                hosts,
                MongoClientOptions.builder().SSLEnabled(plugin.getConfig().getSSL()).build()
        );

        db = client.getDatabase(plugin.getConfig().getDatabase());

        plugin.getConfig().getWorlds().forEach(this::createIndexes);
    }

    public void createIndexes(final String world) {
        getCollection(world).tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("x").addKey("y").addKey("z").build()
        ));
    }

    public void insert(final String world, final Collection<BlockState> documents) {
        for (BlockState bs : documents) {
            db.getCollection(world).insert(bs.toDocument());
        }
    }

    public MongoCollection getCollection(final World world) {
        return db.getCollection(world.getName());
    }

    public MongoCollection getCollection(final String world) {
        return db.getCollection(world);
    }

    public Collection<BlockState> find(final Location location) {
        Collection<BlockState> result = new LinkedList<>();

        try (MongoCursor cursor = getCollection(location.getWorld()).find(getQuery(location)).sort(new Document("_id", -1)).limit(maxResult).get()) {
            while (cursor.hasNext()) {
                result.add(new BlockState((Document) cursor.next()));
            }
        }

        return result;
    }

    Document getQuery(final Location location) {
        return new Document(ImmutableMap.of(
                "x", location.getBlockX(),
                "y", location.getBlockY(),
                "z", location.getBlockZ()
        ));
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
