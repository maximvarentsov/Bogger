package ru.gtncraft.bogger;

import com.google.common.collect.ImmutableList;
import org.bukkit.World;
import org.mongodb.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Storage implements AutoCloseable {

    final int maxResult;
    final MongoClient client;
    final MongoDatabase db;

    public Storage(final Bogger plugin) throws IOException {

        maxResult = plugin.getConfig().getInt("results");

        client = MongoClients.create(plugin.getConfig().getHosts());

        db = client.getDatabase(plugin.getConfig().getString("storage.name"));

        plugin.getConfig().getWorlds().forEach(this::createIndexes);
    }

    public void createIndexes(final String world) {
        getCollection(world).tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("x").addKey("y").addKey("z").build(),
            Index.builder().addKey("_id").build()
        ));
    }

    public void insert(final String world, final Collection<BlockState> documents) {
        try {
            db.getCollection(world).insert((List) documents);
        } catch (MongoDuplicateKeyException ignore) {}
    }

    public MongoCollection getCollection(final World world) {
        return db.getCollection(world.getName());
    }

    public MongoCollection getCollection(final String world) {
        return db.getCollection(world);
    }

    public Collection<BlockState> find(final World world, final BlockState query) {
        Collection<BlockState> result = new LinkedList<>();
        try (MongoCursor cursor = getCollection(world).
                                  find(query).sort(new Document("_id", -1)).limit(maxResult).get()) {
            cursor.forEachRemaining(v -> result.add(new BlockState((Document) v)));
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
