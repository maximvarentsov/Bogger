package ru.gtncraft.bogger;

import com.mongodb.BasicDBObject;
import org.bukkit.Location;
import org.bukkit.Material;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BlockState extends BasicDBObject {

    private final static String format = "dd.MM.yyyy HH:mm:ss";

    public BlockState(final Location location) {
        this.put("x", location.getX());
        this.put("y", location.getY());
        this.put("z", location.getZ());
        this.put("_id", System.currentTimeMillis());
    }

    public BlockState(final Map map) {
        this.putAll(map);
    }

    public void setBlock(final Material value) {
        put("block", value.name());
    }

    public void setPlayer(final String player) {
        put("player", player);
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
        return getInt("action");
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String message = dateFormat.format(getDatetime()) + " " + getPlayer() + " " + getBlock() + " ";
        message += getAction() > 0 ? "place" : "break";
        return message;
    }
}
