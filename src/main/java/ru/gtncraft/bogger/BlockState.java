package ru.gtncraft.bogger;

import com.mongodb.BasicDBObject;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BlockState extends BasicDBObject {

    private final static String format = "dd.MM.yyyy HH:mm:ss";

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
        switch (block.getType()) {
            case LOG:
            case LOG_2:
            case WOOD:
            case WOOD_STEP:
            case SAPLING:
            case LEAVES:
            case LEAVES_2:
            case WOOL:
            case CARPET:
            case LONG_GRASS:
            case RED_ROSE:
            case DOUBLE_PLANT:
            case ANVIL:
            case STAINED_CLAY:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case SMOOTH_BRICK:
            case MONSTER_EGGS:
            case SANDSTONE:
            case COBBLE_WALL:
            case STEP:
            case QUARTZ_BLOCK:
            case DIRT: // DIRT:2 - podzol
            case SAND: // SAND:2 - red sand
                if (block.getData() > 0) {
                    put("block", block.getType().name() + ":" + block.getData());
                    break;
                }
            default:
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
