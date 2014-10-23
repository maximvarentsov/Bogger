package ru.gtncraft.bogger;

import org.bson.types.ObjectId;
import java.util.UUID;

class Log {
    public static enum Action {
        PLACE(1), BREAK(-1);
        private final int intRepresentation;

        private Action(int intRepresentation)
        {
            this.intRepresentation = intRepresentation;
        }

        public int getIntRepresentation()
        {
            return this.intRepresentation;
        }

        public static Action fromInt(int intRepresentation)
        {
            switch (intRepresentation)
            {
                case 1:
                    return PLACE;
                case -1:
                    return BREAK;
            }
            throw new IllegalArgumentException(intRepresentation + " is not a valid index Action");
        }
    }

    public final ObjectId id;
    public final int x;
    public final int y;
    public final int z;
    public final Action action;
    public final UUID uuid;
    public final String block;

    public Log(ObjectId id, int x, int y, int z, Action action, String uuid, String block) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.action = action;
        this.uuid = UUID.fromString(uuid);
        this.block = block;
    }

    public Log(int x, int y, int z, Action action, String uuid, String block) {
        this(new ObjectId(), x, y, z, action, uuid, block);
    }
}
