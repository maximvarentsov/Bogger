package ru.gtncraft.bogger;

import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mongodb.ConvertibleToDocument;
import org.mongodb.Document;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

class BlockState implements ConvertibleToDocument {
    private final Document document;

    public BlockState(final Map<String, Object> map) {
        document = new Document(map);
    }

    public BlockState(Block block, UUID uuid, int action) {
        document = new Document("_id", new ObjectId());
        Location location = block.getLocation();
        document.put("x", location.getBlockX());
        document.put("y", location.getBlockY());
        document.put("z", location.getBlockZ());
        document.put("action", action);
        document.put("uuid", uuid.toString());
        if (block.getData() > 0) {
            document.put("block", block.getType().name() + ":" + block.getData());
        } else {
            document.put("block", block.getType().name());
        }
    }

    public String getBlock() {
        return document.getString("block");
    }

    public Date getDate() {
        return document.getObjectId("_id").getDate();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(document.getString("uuid")));
    }

    public int getAction() {
        return document.getInteger("action");
    }

    @Override
    public Document toDocument() {
        return document;
    }
}
