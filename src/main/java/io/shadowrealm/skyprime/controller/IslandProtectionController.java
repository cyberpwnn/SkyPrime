package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import mortar.bukkit.plugin.Controller;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

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
		if (null == vi || vi.getIsland().getProtection().canBuild(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("break blocks"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBlockPlace(BlockPlaceEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getBlock());
		if (null == vi || vi.getIsland().getProtection().canBuild(e.getPlayer())) return;
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
		if (null == vi || vi.getIsland().getProtection().canUseBlock((Player) e.getPlayer())) {
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

	@EventHandler
	public void playerPickupArrow(PlayerPickupArrowEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canPickup(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("pickup arrows"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerEntityDamage(EntityDamageByEntityEvent e)
	{
		if (!(e.getDamager() instanceof Player)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi) return;

		final Player p = (Player) e.getDamager();

		// PVP?
		// @todo config option to toggle PVP against non-members
		if (e.getEntity() instanceof HumanEntity && !vi.getIsland().getProtection().canPVP(p)) {
			e.getEntity().sendMessage(getMessage("PVP"));
			e.setCancelled(true);
		}

		// mob damage
		else if (e.getEntity() instanceof LivingEntity && !vi.getIsland().getProtection().canKill(p)) {
			e.getEntity().sendMessage(getMessage("harm mobs"));
			e.setCancelled(true);
		}

		// hanging entitiy damage
		else if (e.getEntity() instanceof Hanging && !vi.getIsland().getProtection().canBuild(p)) {
			e.getEntity().sendMessage(getMessage("break hanging items"));
			e.setCancelled(true);
		}

		// armourstands
		else if (e.getEntity() instanceof ArmorStand && !vi.getIsland().getProtection().canBuild(p)) {
			e.getEntity().sendMessage(getMessage("break armour stands"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerEntityInteract(PlayerInteractEntityEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi) return;

		// interact with armorstand
		if (e.getRightClicked() instanceof ArmorStand && !vi.getIsland().getProtection().canUseBlock(e.getPlayer())) {
			e.getPlayer().sendMessage(getMessage("interact with armor stands"));
			e.setCancelled(true);
		}

		// interact with hanging paintings or item frames
		else if (e.getRightClicked() instanceof Hanging && !vi.getIsland().getProtection().canBuild(e.getPlayer()))
		{
			e.getPlayer().sendMessage(getMessage("interact with hanging items"));
			e.setCancelled(true);
		}

		else if (e.getRightClicked() instanceof LivingEntity && !vi.getIsland().getProtection().canInteractEntity(e.getPlayer())) {
			e.getPlayer().sendMessage(getMessage("interact with entities"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerBucketUse(PlayerBucketEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canBuild(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("use buckets"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerEnterBed(PlayerBedEnterEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canUseBlock(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("use beds"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerShearEntity(PlayerShearEntityEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canInteractEntity(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("shear mobs"));
		e.setCancelled(true);
	}
}
