package ru.gtncraft.bogger;

import com.mongodb.*;
import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private DBCollection coll;
    final private int maxResult;

    private DBObject fieldsResult = new BasicDBObject(ImmutableMap.of(
        "_id", false, "datetime", true, "player", true, "block", true, "action", true
    ));

    public Storage(ConfigurationSection config) throws Exception {
        maxResult = config.getInt("results");
        MongoClient mongoClient = new MongoClient(config.getString("host"), config.getInt("port"));
        DB db = mongoClient.getDB(config.getString("name"));
        coll = db.getCollection(config.getString("collection"));
        if (coll.count() < 1) {
            ensureIndex();
        }
    }

    public void insert(DBObject document) {
        coll.insert(document, WriteConcern.SAFE);
    }

    public List<BlockState> find(DBObject query) {
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
