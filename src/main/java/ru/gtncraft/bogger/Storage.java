package ru.gtncraft.bogger;

import com.google.common.collect.ImmutableMap;
import com.mongodb.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Storage implements AutoCloseable {

    private final int maxResult;
    private final MongoClient client;
    private final DB db;
    private final Map<String, Collection<BlockState>> queue = new ConcurrentHashMap<>();

    public Storage(final Bogger plugin) throws IOException {
        this.maxResult = plugin.getConfig().getInt("db.results");
        this.client = new MongoClient(plugin.getConfig().getString("db.host"), plugin.getConfig().getInt("db.port"));
        this.db = this.client.getDB(plugin.getConfig().getString("db.name"));
        for (String world : plugin.getConfig().getStringList("worlds")) {
            String name = world.toLowerCase();
            // Create collections and index.
            if (!db.collectionExists(world)) {
                DBCollection collection = db.createCollection(name, new BasicDBObject("autoIndexId", false));
                collection.createIndex(new BasicDBObject("x", 1).append("y", 1).append("z", 1));
                collection.createIndex(new BasicDBObject("_id", 1));
            }
            queue.put(name, new ArrayList<BlockState>());
        }
        // Flush queue every 40 tick.
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Collection<BlockState>> entry : queue.entrySet()) {
                    final Collection<BlockState> blockStates = new ArrayList<>(queue.size());
                    final Iterator<BlockState> it = entry.getValue().iterator();
                    while (it.hasNext()) {
                        blockStates.add(it.next());
                        it.remove();
                    }
                    insert(entry.getKey(), blockStates.toArray(new BlockState[blockStates.size()]));
                }
            }
        }, 0L, 40L);
    }

    private void insert(final String world, final BlockState[] documents) {
        DBCollection collection = db.getCollection(world);
        collection.insert(documents);
    }

    public void queue(final World world, final BlockState document) {
        queue.get(world.getName().toLowerCase()).add(document);
    }

    public Collection<BlockState> find(final Location location) {
        Collection<BlockState> result = new LinkedList<>();
        DBCollection collection = db.getCollection(location.getWorld().getName());
        DBObject query = new BasicDBObject(ImmutableMap.of(
            "x", location.getBlockX(), "y", location.getBlockY(), "z", location.getBlockZ()
        ));
        try (DBCursor cursor = collection.find(query)) {
            cursor.sort(new BasicDBObject("_id", -1)).limit(maxResult);
            while (cursor.hasNext()) {
                result.add(new BlockState(cursor.next().toMap()));
            }
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
