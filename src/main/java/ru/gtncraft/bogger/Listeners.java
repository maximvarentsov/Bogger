package ru.gtncraft.bogger;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.SimpleDateFormat;

class Listeners implements Listener {

    final Material material;
    final Bogger plugin;
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public Listeners(final Bogger plugin) {
        material = Material.matchMaterial(plugin.getConfig().getString("tool", Material.YELLOW_FLOWER.name()));
        if (material == null) {
            plugin.getLogger().warning("Logger tool not found or invalid.");
        }
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final World world = block.getWorld();
        plugin.getQueue().add(world, new BlockState(block, event.getPlayer(), -1));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onBlockPlace(final BlockPlaceEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        plugin.getQueue().add(world, new BlockState(block, event.getPlayer(), 1));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            if (player.getItemInHand().getType() == material) {
                Location clickedBlock = event.getClickedBlock().getLocation().clone();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (BlockState state : plugin.getStorage().find(clickedBlock)) {
                        String message = dateFormat.format(state.getDate()) + " ";
                        message += state.getPlayer().getName() + " ";
                        message += state.getBlock() + " ";
                        message += state.getAction() > 0 ? "place" : "break";
                        player.sendMessage(ChatColor.DARK_AQUA + message);
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
