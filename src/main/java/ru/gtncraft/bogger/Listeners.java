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

class Listeners implements Listener {
    private final Bogger plugin;

    public Listeners(final Bogger plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBlockBreak(final BlockBreakEvent event) {
        plugin.blockBreak(event.getBlock(), event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBlockPlace(final BlockPlaceEvent event) {
        plugin.blockPaste(event.getBlock(), event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            final Player player = event.getPlayer();
            if (player.getItemInHand().getType() == plugin.getLoggerTool()) {
                final Location clickedBlock = event.getClickedBlock().getLocation().clone();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(plugin.history(clickedBlock));
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
