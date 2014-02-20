package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

public class Listeners implements Listener {

    private final Bogger plugin;
    private final Material material;
    private final Collection<String> worlds;
    private final Storage storage;

    public Listeners(final Bogger plugin) {
        String materialName = plugin.getConfig().getString("material");
        material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Material not found " + materialName);
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        worlds = plugin.getConfig().getStringList("worlds");

        this.storage = plugin.getStorage();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (isLogging(block)) {
            BlockState state = new BlockState(block.getLocation());
            state.setBlock(block);
            state.setPlayer(event.getPlayer().getName());
            state.setAction(-1);
            storage.queue(block.getWorld(), state);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (isLogging(block)) {
            BlockState state = new BlockState(block.getLocation());
            state.setBlock(block);
            state.setPlayer(event.getPlayer().getName());
            state.setAction(1);
            storage.queue(block.getWorld(), state);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType().equals(material)) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                final Location query = event.getClickedBlock().getLocation().clone();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (BlockState state : plugin.getStorage().find(query)) {
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
