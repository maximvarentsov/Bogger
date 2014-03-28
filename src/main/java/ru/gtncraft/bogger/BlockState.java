package ru.gtncraft.bogger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mongodb.Document;

import java.util.Date;
import java.util.Map;

public class BlockState extends Document {

    public BlockState(final Block block, final Player player, final int action) {
        this.setLocation(block.getLocation());
        this.setBlock(block);
        this.setPlayer(player);
        this.setAction(action);
        this.setDatetime();
    }

    public BlockState(final Location location) {
        this.setLocation(location);
    }

    public BlockState(final Map map) {
        this.putAll(map);
    }

    public void setDatetime() {
        put("_id", System.currentTimeMillis());
    }

    public void setLocation(final Location location) {
        put("x", location.getX());
        put("y", location.getY());
        put("z", location.getZ());
    }

    public void setBlock(final Block block) {
        if (block.getData() > 0) {
            put("block", block.getType().name() + ":" + block.getData());
        } else {
            put("block", block.getType().name());
        }
    }

    public void setPlayer(final Player player) {
        put("player", player.getName().toLowerCase());
    }

    public void setAction(final int value) {
        put("action", value);
    }

    public String getBlock() {
        return getString("block");
    }

    public Date getDatetime() {
        return new Date(getLong("_id"));
    }

    public String getPlayer() {
        return getString("player");
    }

    public int getAction() {
        return getInteger("action");
    }
}
