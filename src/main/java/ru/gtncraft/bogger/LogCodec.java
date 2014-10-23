package ru.gtncraft.bogger;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

class LogCodec implements CollectibleCodec<Log> {
    @Override
    public boolean documentHasId(final Log value) {
        return true;
    }

    @Override
    public BsonObjectId getDocumentId(final Log value) {
        return new BsonObjectId(value.id);
    }

    @Override
    public void generateIdIfAbsentFromDocument(final Log value) {
    }

    @Override
    public void encode(final BsonWriter writer, final Log value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", value.id);
        writer.writeInt32("x", value.x);
        writer.writeInt32("y", value.y);
        writer.writeInt32("z", value.z);
        writer.writeInt32("action", value.action.getIntRepresentation());
        writer.writeString("uuid", value.uuid.toString());
        writer.writeString("block", value.block);
        writer.writeEndDocument();
    }

    @Override
    public Log decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        int x = reader.readInt32("x");
        int y = reader.readInt32("y");
        int z = reader.readInt32("z");
        int action = reader.readInt32("action");
        String uuid = reader.readString("uuid");
        String block = reader.readString("block");
        reader.readEndDocument();
        return new Log(id, x, y, z, Log.Action.fromInt(action), uuid, block);
    }

    @Override
    public Class<Log> getEncoderClass() {
        return Log.class;
    }
}

