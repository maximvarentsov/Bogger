package ru.gtncraft.bogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Date;

public class BoggerListener implements Listener {

    private BoggerPlugin plugin;
    private Storage st;
    private Material material;

    public BoggerListener(BoggerPlugin plugin) throws Exception {

        st = new Storage(plugin.getConfig().getConfigurationSection("db"));

        String materialName = plugin.getConfig().getString("material");
        material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new Exception("Material not found " + materialName);
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final BlockState state = new BlockState(event.getBlock().getLocation());
        state.setDatetime(new Date());
        state.setBlock(event.getBlock().getType().name());
        state.setPlayer(event.getPlayer().getName());
        state.setAction(-1);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                st.insert(state);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final BlockState state = new BlockState(event.getBlock().getLocation());
        state.setDatetime(new Date());
        state.setBlock(event.getBlock().getType().name());
        state.setPlayer(event.getPlayer().getName());
        state.setAction(1);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                st.insert(state);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType().equals(material)) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                final BlockState query = new BlockState(event.getClickedBlock().getLocation());
                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        for (BlockState state : st.find(query)) {
                            player.sendMessage(ChatColor.DARK_AQUA + "" + state);
                        }
                    }
                });
                event.setCancelled(true);
            }
        }
    }
}
