package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.*;

public class Listeners implements Listener {

    private final Bogger plugin;
    private final Storage st;
    private final Material material;
    private final Collection<String> worlds;
    private final Collection<BlockState> queue;

    public Listeners(final Bogger plugin) throws IOException {

        st = new Storage(plugin.getConfig().getConfigurationSection("db"));
        queue = Collections.synchronizedList(new ArrayList<BlockState>());
        String materialName = plugin.getConfig().getString("material");
        material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Material not found " + materialName);
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        worlds = (List<String>) plugin.getConfig().getList("worlds");

        this.plugin = plugin;

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (queue.size() == 0) {
                    return;
                }
                Iterator<BlockState> it = queue.iterator();
                while (it.hasNext()) {
                    st.insert(it.next());
                    it.remove();
                }
            }
        }, 0L, 40L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (isLogging(block)) {
            BlockState state = new BlockState(block.getLocation());
            state.setDatetime(new Date());
            state.setBlock(block.getType());
            state.setPlayer(event.getPlayer().getName());
            state.setAction(-1);
            queue.add(state);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (isLogging(block)) {
            BlockState state = new BlockState(block.getLocation());
            state.setDatetime(new Date());
            state.setBlock(block.getType());
            state.setPlayer(event.getPlayer().getName());
            state.setAction(1);
            queue.add(state);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType().equals(material)) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                final BlockState query = new BlockState(event.getClickedBlock().getLocation());
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (BlockState state : st.find(query)) {
                            player.sendMessage(ChatColor.DARK_AQUA + state.toString());
                        }
                    }
                });
                event.setCancelled(true);
            }
        }
    }

    private boolean isLogging(final Block block) {
        return worlds.contains(block.getWorld().getName());
    }
}
