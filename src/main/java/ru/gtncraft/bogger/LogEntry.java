package ru.gtncraft.bogger;

import org.bson.types.ObjectId;
import java.util.UUID;

public class LogEntry {
    private final ObjectId id;
    private final int x;
    private final int y;
    private final int z;
    private final int action;
    private final UUID uuid;
    private final String block;

    public LogEntry(ObjectId id, int x, int y, int z, int action, String uuid, String block) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.action = action;
        this.uuid = UUID.fromString(uuid);
        this.block = block;
    }

    public LogEntry(int x, int y, int z, int action, String uuid, String block) {
        this(new ObjectId(), x, y, z, action, uuid, block);
    }

    public ObjectId getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getAction() {
        return action;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getBlock() {
        return block;
    }
}
