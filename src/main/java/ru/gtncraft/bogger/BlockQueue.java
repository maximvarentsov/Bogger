package ru.gtncraft.bogger;

import org.bukkit.World;

import java.util.*;
import java.util.stream.Stream;

public class BlockQueue {

    final Map<String, Collection<BlockState>> values = new HashMap<>();

    public BlockQueue(final Stream<String> worlds) {
        worlds.forEach(
            w -> values.put(w, Collections.synchronizedList(new ArrayList<>()))
        );
    }

    public Map<String, Collection<BlockState>> flush() {
        Map<String, Collection<BlockState>> result = new HashMap<>();
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
