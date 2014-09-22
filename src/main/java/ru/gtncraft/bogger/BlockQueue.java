package ru.gtncraft.bogger;

import org.bukkit.World;

import java.util.*;

class BlockQueue {
    private final Map<String, List<BlockState>> values = new HashMap<>();

    public BlockQueue(final List<String> worlds) {
        worlds.forEach(
            w -> values.put(w, Collections.synchronizedList(new ArrayList<>()))
        );
    }

    public Map<String, List<BlockState>> flush() {
        Map<String, List<BlockState>> result = new HashMap<>();
        values.entrySet().forEach(entry -> {
            for (Iterator<BlockState> it = entry.getValue().iterator(); it.hasNext();) {
                if (!result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), new ArrayList<>());
                }
                result.get(entry.getKey()).add(it.next());
                it.remove();
            }
        });
        return result;
    }

    public boolean add(final World world, final BlockState document) {
        if (values.containsKey(world.getName())) {
            values.get(world.getName()).add(document);
            return true;
        }
        return false;
    }
}
