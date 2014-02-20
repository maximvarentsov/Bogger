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

public class Listeners implements Listener {

    private final Material material;
    private final Storage storage;
    private final Bogger plugin;

    public Listeners(final Bogger plugin, final Storage storage) {
        material = Material.matchMaterial(plugin.getConfig().getString("material", Material.YELLOW_FLOWER.name()));
        if (material == null) {
            plugin.getLogger().warning("Logger tool not found or invalid.");
        }
        this.storage = storage;
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final World world = block.getWorld();
        if (storage.isLogging(world)) {
            storage.queue(world, new BlockState(block, event.getPlayer(), -1));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final World world = block.getWorld();
        if (storage.isLogging(world)) {
            storage.queue(world, new BlockState(block, event.getPlayer(), 1));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            final Player player = event.getPlayer();
            if (player.getItemInHand().getType() == material) {
                final World world = player.getWorld();
                final BlockState query = new BlockState(event.getClickedBlock().getLocation());
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (BlockState state : storage.find(world, query)) {
                            player.sendMessage(ChatColor.DARK_AQUA + state.toString());
                        }
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
