package ru.gtncraft.bogger;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOptions;
import org.bson.Document;
import org.bukkit.Location;

import java.util.*;

class LogWorld {
    private final List<Log> logs = Collections.synchronizedList(new ArrayList<Log>());
    private final MongoCollection<Log> collection;
    private final FindOptions findOptions;
    public static int limit = 10;

    public LogWorld(MongoCollection<Log> collection) {
        this.collection = collection;
        this.findOptions = new FindOptions().limit(limit).sort(new Document("_id", -1));
        this.collection.createIndex(new Document("x", 1).append("y", 1).append("z", 1));
    }

    public Collection<Log> find(Location location) {
        return collection.find(location, findOptions).into(new LinkedList<Log>());
    }

    public int save() {
        List<Log> values = new ArrayList<Log>();
        for (Iterator<Log> it = values.iterator(); it.hasNext();) {
            values.add(it.next());
            it.remove();
        }
        if (values.size() > 0) {
            collection.insertMany(values);
        }
        return values.size();
    }

    public void add(Log log) {
        logs.add(log);
    }
}


