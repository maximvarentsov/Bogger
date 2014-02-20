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

    private final Bogger plugin;
    private final Material material;
    private final Storage storage;

    public Listeners(final Bogger plugin) {
        String materialName = plugin.getConfig().getString("material");
        material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Material not found " + materialName);
        }
        this.storage = plugin.getStorage();
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
        if (storage.isLogging(event.getBlock().getWorld())) {
            storage.queue(world, new BlockState(event.getBlock(), event.getPlayer(), 1));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType().equals(material)) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                final World world = player.getWorld();
                final BlockState query = new BlockState(event.getClickedBlock().getLocation());
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (BlockState state : plugin.getStorage().find(world, query)) {
                            player.sendMessage(ChatColor.DARK_AQUA + state.toString());
                        }
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
