package ru.gtncraft.bogger;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoDatabaseOptions;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

class LogManager implements AutoCloseable, Iterable<LogWorld> {
    private final Map<String, LogWorld> worlds = new HashMap<String, LogWorld>();
    private final MongoClient client;
    private final MongoDatabase db;

    public LogManager(String host, String database) throws Exception {
        client = new MongoClient(host);
        List<CodecProvider> codecs = Arrays.asList(new DocumentCodecProvider(),
                new CodecProvider() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
                        if (clazz.equals(Log.class)) {
                            return (Codec<T>) new LogCodec();
                        }
                        if (clazz.equals(Location.class)) {
                            return (Codec<T>) new LocationCodec();
                        }
                        return null;
                    }
                }
        );
        MongoDatabaseOptions options = MongoDatabaseOptions.builder().
                                       codecRegistry(new RootCodecRegistry(codecs)).
                                       readPreference(ReadPreference.nearest()).
                                       writeConcern(WriteConcern.SAFE).build();
        db = client.getDatabase(database, options);
    }

    public void register(String world) {
        MongoCollection<Log> collection = db.getCollection(world, Log.class);
        worlds.put(world, new LogWorld(collection));
    }

    public void add(Block block, Player player, Log.Action action) {
        LogWorld log = worlds.get(block.getWorld().getName());
        if (log == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        String blockName = block.getType().name();
        if (block.getData() > 0) {
            blockName +=":" + block.getData();
        }

        Log value = new Log(block.getX(), block.getY(), block.getZ(), action, uuid.toString(), blockName);
        log.add(value);
    }

    public Collection<Log> find(Location location) {
        LogWorld log = worlds.get(location.getWorld().getName());
        if (log == null) {
            return new ArrayList<Log>();
        }
        return log.find(location);
    }

    @Override
    public void close() throws Exception {
        worlds.clear();
        client.close();
    }

    @Override
    public Iterator<LogWorld> iterator() {
        return worlds.values().iterator();
    }
}

