package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

class BlockQueue {
    private final Map<String, List<LogEntry>> blocks = new HashMap<String, List<LogEntry>>();

    public BlockQueue(final Bogger plugin) {
        for (String world : plugin.getConfig().getStringList("worlds")) {
            blocks.put(world, Collections.synchronizedList(new ArrayList<LogEntry>()));
        }
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, List<LogEntry>> entry : blocks.entrySet()) {
                    String world = entry.getKey();
                    List<LogEntry> blocks = new ArrayList<LogEntry>();
                    for (Iterator<LogEntry> it = entry.getValue().iterator(); it.hasNext();) {
                        blocks.add(it.next());
                        it.remove();
                    }
                    if (blocks.size() > 0) {
                        plugin.getStorage().insert(world, blocks);
                    }
                }
            }
        }, 120L, 40L);
    }

    public void add(Block block, Player player, int action) {
        World world = block.getWorld();
        if (blocks.containsKey(world.getName())) {
            UUID uuid = player.getUniqueId();
            LogEntry value = new LogEntry(block.getX(), block.getY(), block.getZ(), action, uuid.toString(), "");
            blocks.get(world.getName()).add(value);
        }
    }
}
