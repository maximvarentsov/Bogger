package ru.gtncraft.bogger;

import com.google.common.collect.ImmutableMap;
import com.mongodb.*;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private final DBCollection coll;
    private final int maxResult;

    private DBObject fieldsResult = new BasicDBObject(ImmutableMap.of(
        "_id", false, "datetime", true, "player", true, "block", true, "action", true
    ));

    public Storage(final ConfigurationSection config) throws IOException {
        maxResult = config.getInt("results");
        MongoClient mongoClient = new MongoClient(config.getString("host"), config.getInt("port"));
        DB db = mongoClient.getDB(config.getString("name"));
        coll = db.getCollection(config.getString("collection"));
        if (coll.count() < 1) {
            ensureIndex();
        }
    }

    public void insert(final DBObject document) {
        coll.insert(document);
    }

    public List<BlockState> find(final DBObject query) {
        List<BlockState> result = new ArrayList<>(maxResult);
        DBCursor cursor = coll.find(query, fieldsResult);
        cursor.hint("datetime_1").limit(maxResult);
        while (cursor.hasNext()) {
            result.add(new BlockState(cursor.next().toMap()));
        }
        return result;
    }

    private void ensureIndex() {
        coll.createIndex(new BasicDBObject("world", 1).append("x", 1).append("y", 1).append("z", 1));
        coll.createIndex(new BasicDBObject("datetime", 1));
    }
}
