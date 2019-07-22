package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import mortar.bukkit.plugin.Controller;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class IslandProtectionController extends Controller implements Listener
{
	@Override
	public void start()
	{
	}

	@Override
	public void stop()
	{
	}

	@Override
	public void tick()
	{
	}

	protected VirtualIsland getIsland(Block block)
	{
		return this.getIsland(block.getLocation());
	}

	protected VirtualIsland getIsland(Location location)
	{
		return SkyMaster.getIsland(location.getWorld());
	}

	public final String getMessage(String action)
	{
		return ChatColor.RED + "You are not allowed to " + action + " in this island.";
	}

	@EventHandler
	public void playerBlockBreak(BlockBreakEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getBlock());
		if (null == vi || vi.getIsland().getProtection().canBreak(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("break blocks"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBlockPlace(BlockPlaceEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getBlock());
		if (null == vi || vi.getIsland().getProtection().canBreak(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("place blocks"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerInventoryOpen(InventoryOpenEvent e)
	{
		if (!(e.getInventory().getHolder() instanceof BlockState)) {
			return;
		}

		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canUse((Player) e.getPlayer())) {
			if (vi != null) vi.modified();
			return;
		}
		e.getPlayer().sendMessage(getMessage("use blocks"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerPickup(EntityPickupItemEvent e)
	{
		if (!(e.getEntity() instanceof Player)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canPickup((Player) e.getEntity())) return;
		e.getEntity().sendMessage(getMessage("pickup items"));
		e.setCancelled(true);
	}
}
