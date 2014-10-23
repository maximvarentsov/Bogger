package ru.gtncraft.bogger;

import org.bson.types.ObjectId;
import java.util.UUID;

class Log {
    public final ObjectId id;
    public final int x;
    public final int y;
    public final int z;
    public final int action;
    public final UUID uuid;
    public final String block;

    public Log(ObjectId id, int x, int y, int z, int action, String uuid, String block) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.action = action;
        this.uuid = UUID.fromString(uuid);
        this.block = block;
    }

    public Log(int x, int y, int z, int action, String uuid, String block) {
        this(new ObjectId(), x, y, z, action, uuid, block);
    }
}
