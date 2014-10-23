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
import org.bukkit.Location;

public class LocationCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (clazz.equals(Location.class)) {
            return (Codec<T>) new LocationCodec();
        }
        return null;
    }
}

class LocationCodec implements CollectibleCodec<Location> {
    @Override
    public boolean documentHasId(final Location value) {
        return false;
    }

    @Override
    public BsonObjectId getDocumentId(final Location value) {
        return null;
    }

    @Override
    public void generateIdIfAbsentFromDocument(final Location value) {
    }

    @Override
    public void encode(final BsonWriter writer, final Location value, final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32("x", value.getBlockX());
        writer.writeInt32("y", value.getBlockY());
        writer.writeInt32("z", value.getBlockZ());
        writer.writeEndDocument();
    }

    @Override
    public Location decode(final BsonReader reader, final DecoderContext decoderContext) {
        return null;
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
