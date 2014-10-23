package ru.gtncraft.bogger.codecs;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import ru.gtncraft.bogger.LogEntry;

public class LogEntryCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (clazz.equals(LogEntryCodec.class)) {
            return (Codec<T>) new LogEntryCodec();
        }
        return null;
    }
}

class LogEntryCodec implements CollectibleCodec<LogEntry> {
    @Override
    public boolean documentHasId(final LogEntry document) {
        return true;
    }

    @Override
    public BsonObjectId getDocumentId(final LogEntry document) {
        return new BsonObjectId(document.getId());
    }

    @Override
    public void generateIdIfAbsentFromDocument(final LogEntry person) {
    }

    @Override
    public void encode(final BsonWriter writer, final LogEntry value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", value.getId());
        writer.writeInt32("x", value.getX());
        writer.writeInt32("y", value.getY());
        writer.writeInt32("z", value.getZ());
        writer.writeInt32("action", value.getAction());
        writer.writeString("uuid", value.getUUID().toString());
        writer.writeString("block", value.getBlock());
        writer.writeEndDocument();
    }

    @Override
    public LogEntry decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        int x = reader.readInt32("x");
        int y = reader.readInt32("y");
        int z = reader.readInt32("z");
        int action = reader.readInt32("action");
        String uuid = reader.readString("uuid");
        String block = reader.readString("block");
        reader.readEndDocument();
        return new LogEntry(id, x, y, z, action, uuid, block);
    }

    @Override
    public Class<LogEntry> getEncoderClass() {
        return LogEntry.class;
    }
}
