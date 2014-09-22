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

class BlockState extends Document {
    public BlockState(final Map<String, Object> map) {
        putAll(map);
    }

    public BlockState(Block block, UUID uuid, int action) {
        put("_id", new ObjectId());
        Location location = block.getLocation();
        put("x", location.getBlockX());
        put("y", location.getBlockY());
        put("z", location.getBlockZ());
        put("action", action);
        put("uuid", uuid.toString());
        if (block.getData() > 0) {
            put("block", block.getType().name() + ":" + block.getData());
        } else {
            put("block", block.getType().name());
        }
    }

    public String getBlock() {
        return getString("block");
    }

    public Date getDate() {
        return getObjectId("_id").getDate();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(getString("uuid")));
    }

    public int getAction() {
        return getInteger("action");
    }
}
