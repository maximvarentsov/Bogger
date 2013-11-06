package ru.gtncraft.bogger;

import com.mongodb.BasicDBObject;
import org.bukkit.Location;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BlockState extends BasicDBObject {

    public String format = "dd.MM.yyyy HH:mm:ss";

    public BlockState(Location loc) {
        put("world", loc.getWorld().getName());
        put("x", loc.getX());
        put("y", loc.getY());
        put("z", loc.getZ());
    }

    public BlockState(Map map) {
        putAll(map);
    }

    public void setBlock(String value) {
        put("block", value);
    }

    public void setDatetime(Date value) {
        put("datetime", value);
    }

    public void setPlayer(String player) {
        put("player", player);
    }

    public void setAction(int value) {
        put("action", value);
    }

    public String getBlock() {
        return getString("block");
    }

    public Date getDatetime() {
        return getDate("datetime");
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
