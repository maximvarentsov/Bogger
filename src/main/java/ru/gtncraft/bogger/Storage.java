package ru.gtncraft.bogger;

import com.mongodb.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.*;

public class Storage implements AutoCloseable {

    private final int maxResult;
    private final MongoClient client;
    private final DB db;
    private final Map<String, Collection<BlockState>> queue = new HashMap<>();

    public Storage(final Bogger plugin) throws IOException {
        this.maxResult = plugin.getConfig().getInt("db.results");
        this.client = new MongoClient(plugin.getConfig().getString("db.host"), plugin.getConfig().getInt("db.port"));
        this.db = this.client.getDB(plugin.getConfig().getString("db.name"));
        for (String world : plugin.getConfig().getStringList("worlds")) {
            world = world.toLowerCase();
            // Create collections and index.
            if (!db.collectionExists(world)) {
                final DBCollection collection = db.createCollection(world, new BasicDBObject("autoIndexId", false));
                collection.createIndex(new BasicDBObject("x", 1).append("y", 1).append("z", 1));
                collection.createIndex(new BasicDBObject("_id", 1));
            }
            queue.put(world, Collections.synchronizedList(new ArrayList<BlockState>()));
        }
        // Flush queue every 40 tick.
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Collection<BlockState>> entry : queue.entrySet()) {
                    final Collection<BlockState> values = new ArrayList<>();
                    for (Iterator<BlockState> it = entry.getValue().iterator(); it.hasNext(); ) {
                        values.add(it.next());
                        it.remove();
                    }
                    insert(entry.getKey(), values.toArray(new BlockState[values.size()]));
                }
            }
        }, 0L, 40L);
    }

    private void insert(final String world, final BlockState[] documents) {
        db.getCollection(world).insert(documents);
    }

    public void queue(final World world, final BlockState document) {
        queue.get(world.getName().toLowerCase()).add(document);
    }

    public boolean isLogging(final World world) {
        return queue.containsKey(world.getName().toLowerCase());
    }

    public Collection<BlockState> find(final World world, final BlockState query) {
        final Collection<BlockState> result = new LinkedList<>();
        final String name = world.getName().toLowerCase();
        if (queue.containsKey(name)) {
            final DBCollection collection = db.getCollection(name);
            try (final DBCursor cursor = collection.find(new BlockState(query))) {
                cursor.sort(new BasicDBObject("_id", -1)).limit(maxResult);
                while (cursor.hasNext()) {
                    result.add(new BlockState(cursor.next().toMap()));
                }
            }
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
