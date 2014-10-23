package ru.gtncraft.bogger;

import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Location;

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
