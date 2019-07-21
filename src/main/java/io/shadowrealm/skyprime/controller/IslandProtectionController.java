package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.VirtualIsland;
import mortar.bukkit.plugin.Controller;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
		return null;
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
}
