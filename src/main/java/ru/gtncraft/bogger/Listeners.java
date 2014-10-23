package ru.gtncraft.bogger;

import org.bukkit.*;
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
    private final SimpleDateFormat dateFormat;
    private final Material material;
    private final Bogger plugin;
    private final BlockQueue queue;

    public Listeners(final Bogger plugin) {
        dateFormat = new SimpleDateFormat(plugin.getConfig().getString("dateFormat", "dd.MM.yyyy HH:mm:ss"));
        material = Material.matchMaterial(plugin.getConfig().getString("tool", Material.YELLOW_FLOWER.name()));
        if (material == null) {
            plugin.getLogger().warning("Logger tool not found or invalid.");
        }
        queue = new BlockQueue(plugin);
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBlockBreak(final BlockBreakEvent event) {
        queue.add(event.getBlock(), event.getPlayer(), -1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBlockPlace(final BlockPlaceEvent event) {
        queue.add(event.getBlock(), event.getPlayer(), 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            final Player player = event.getPlayer();
            if (player.getItemInHand().getType() == material) {
                final Location clickedBlock = event.getClickedBlock().getLocation().clone();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (LogEntry state : plugin.getStorage().find(clickedBlock)) {
                            String message = dateFormat.format(state.getId().getDate()) + " ";
                            message += Bukkit.getOfflinePlayer(state.getUUID()).getName() + " ";
                            message += state.getBlock() + " ";
                            message += state.getAction() == 1 ? "place" : "break";
                            player.sendMessage(ChatColor.DARK_AQUA + message);
                        }
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
