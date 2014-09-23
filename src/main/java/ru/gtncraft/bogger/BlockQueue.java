package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.mongodb.Document;

import java.util.*;

class BlockQueue {
    private final Map<String, List<Document>> blocks = new HashMap<>();

    public BlockQueue(final Bogger plugin) {
        for (String world : plugin.getConfig().getStringList("worlds")) {
            blocks.put(world, Collections.synchronizedList(new ArrayList<>()));
        }
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, List<Document>> entry : blocks.entrySet()) {
                    String world = entry.getKey();
                    List<Document> blocks = new ArrayList<>();
                    for (Iterator<Document> it = entry.getValue().iterator(); it.hasNext();) {
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

    public boolean add(final World world, final BlockState document) {
        if (blocks.containsKey(world.getName())) {
            blocks.get(world.getName()).add(document.toDocument());
            return true;
        }
        return false;
    }
}
