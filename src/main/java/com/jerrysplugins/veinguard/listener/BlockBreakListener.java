package com.jerrysplugins.veinguard.listener;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final VeinGuard plugin;

    public BlockBreakListener(VeinGuard plugin) {
        this.plugin = plugin;
        plugin.getLog().log(Level.DEBUG, "Registering listener BlockBreakListener.class");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("veinguard.bypass")) return;

        Location location = event.getBlock().getLocation();
        World world = player.getWorld();
        Material blockType = event.getBlock().getType();

        if(plugin.getConfigOptions().isWorldDisabled(world)) return;
        if(location.getBlockY() > plugin.getConfigOptions().getIgnoreAboveY()) return;
        if(plugin.getConfigOptions().isIgnoreCreative() && player.getGameMode() == GameMode.CREATIVE) return;
        if(!plugin.getConfigOptions().isTrackedMaterial(blockType)) return;
        if(plugin.getConfigOptions().isIgnoredTool(player.getInventory().getItemInMainHand().getType())) return;

        plugin.getPlayerTracker().recordBreak(player, blockType, location);
    }
}