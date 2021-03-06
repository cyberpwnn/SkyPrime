package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import mortar.api.fulcrum.util.PlayerBlockEvent;
import mortar.bukkit.plugin.Controller;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class IslandProtectionController extends Controller implements Listener
{

	public static final Material[] useableTypes = new Material[] {
			// chests
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.ENDER_CHEST,

			// interactive items
			Material.FURNACE,
			Material.BURNING_FURNACE,
			Material.ANVIL,
			Material.DISPENSER,
			Material.DROPPER,
			Material.HOPPER,
			Material.COMMAND,
			Material.ENCHANTMENT_TABLE,
			Material.WORKBENCH,
			Material.BREWING_STAND,
			Material.CAULDRON,
			Material.JUKEBOX
	};

	/**
	 * What items can players interact with
	 * @todo move to mortar
	 */
	public static final Material[] interactiveTypes = new Material[] {
			// redstone
			Material.LEVER,
			Material.STONE_BUTTON,
			Material.WOOD_BUTTON,
			Material.IRON_PLATE,
			Material.GOLD_PLATE,
			Material.STONE_PLATE,
			Material.WOOD_PLATE,
			Material.REDSTONE_COMPARATOR,
			Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON,
			Material.DIODE,
			Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON,
			Material.NOTE_BLOCK,

			// doors
			Material.IRON_TRAPDOOR,
			Material.ACACIA_DOOR,
			Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR,
			Material.IRON_DOOR,
			Material.IRON_DOOR_BLOCK,
			Material.JUNGLE_DOOR,
			Material.SPRUCE_DOOR,
			Material.TRAP_DOOR,
			Material.WOOD_DOOR,
			Material.WOODEN_DOOR,

			// gates
			Material.ACACIA_FENCE_GATE,
			Material.BIRCH_FENCE_GATE,
			Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,
			Material.FENCE_GATE,
			Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE
	};

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
	public void playerBlockInteract(PlayerInteractEvent e)
	{
		if (e.getItem() != null) {
			final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
			if (vi != null && e.getItem().getType().equals(Material.MONSTER_EGG) && !vi.getIsland().getProtection().canKill(e.getPlayer())) {
				e.getPlayer().sendMessage(getMessage("spawn entities"));
				e.setCancelled(true);
				return;
			} else if (vi != null && e.getItem().getType().equals(Material.ARMOR_STAND) && !vi.getIsland().getProtection().canBuild(e.getPlayer())) {
				e.getPlayer().sendMessage(getMessage("build armorstands"));
				e.setCancelled(true);
				return;
			}
		}

		if (e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.AIR)) return;

		boolean canUse = ArrayUtils.contains(interactiveTypes, e.getClickedBlock().getType());
		boolean canInteract = ArrayUtils.contains(useableTypes, e.getClickedBlock().getType());
		if (!canUse && !canInteract) return;

		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (vi == null) return;

		if (canInteract && !vi.getIsland().getProtection().canInteractBlock(e.getPlayer())) {
			e.getPlayer().sendMessage(getMessage("interact with blocks"));
			e.setCancelled(true);
		} else if (canUse && !vi.getIsland().getProtection().canUseBlock(e.getPlayer())) {
			e.getPlayer().sendMessage(getMessage("use blocks"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerEntityDamage(EntityDamageByEntityEvent e)
	{
		if (!(e.getDamager() instanceof Player || e.getDamager() instanceof Projectile)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi) return;

		final Player p = e.getDamager() instanceof Player ? (Player) e.getDamager() : (Player) ((Projectile) e.getDamager()).getShooter();

		// PVP
		// @todo config option to toggle PVP against non-members
		if (e.getEntity() instanceof HumanEntity && !vi.getIsland().getProtection().canPVP(p)) {
			p.sendMessage(getMessage("PVP"));
			e.setCancelled(true);
		}

		// armourstands
		else if (e.getEntity() instanceof ArmorStand && !vi.getIsland().getProtection().canBuild(p)) {
			p.sendMessage(getMessage("break armour stands"));
			e.setCancelled(true);
		}

		// hanging entities
		else if (e.getEntity() instanceof Hanging && !vi.getIsland().getProtection().canBuild(p)) {
			p.sendMessage(getMessage("break hanging items"));
			e.setCancelled(true);
		}

		// mob damage
		else if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player) && !vi.getIsland().getProtection().canKill(p)) {
			p.sendMessage(getMessage("harm mobs"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerVehicleDamage(VehicleDamageEvent e)
	{
		if (!(e.getAttacker() instanceof Player) || e.getVehicle() instanceof LivingEntity) return;
		final VirtualIsland vi = this.getIsland(e.getVehicle().getLocation());
		if (null == vi || vi.getIsland().getProtection().canBuild((Player) e.getAttacker())) return;
		e.getAttacker().sendMessage(getMessage("break vehicles"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerHangingBreak(HangingBreakByEntityEvent e)
	{
		if (!(e.getRemover() instanceof Player)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canBuild((Player) e.getRemover())) return;
		e.getRemover().sendMessage(getMessage("break hanging items"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerHangingPlace(HangingPlaceEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canBuild(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("place hanging items"));
		e.setCancelled(true);
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

		else if (e.getRightClicked() instanceof Vehicle && !vi.getIsland().getProtection().canInteractEntity(e.getPlayer())) {
			e.getPlayer().sendMessage(getMessage("interact with vehicles"));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerBucketEmpty(PlayerBucketEmptyEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canBuild(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("use buckets"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBucketFill(PlayerBucketFillEvent e)
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
	public void playerArmorStandFuck(PlayerArmorStandManipulateEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getPlayer().getLocation());
		if (null == vi || vi.getIsland().getProtection().canUseBlock(e.getPlayer())) return;
		e.getPlayer().sendMessage(getMessage("use armorstands"));
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

	@EventHandler
	public void playerMountEvent(EntityMountEvent e)
	{
		if (!(e.getEntity() instanceof Player && e.getMount() instanceof Vehicle)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canInteractEntity((Player) e.getEntity())) return;
		e.getEntity().sendMessage(getMessage("ride entities"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerTameEntity(EntityTameEvent e)
	{
		if (!(e.getOwner() instanceof Player)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canInteractEntity((Player) e.getEntity())) return;
		e.getEntity().sendMessage(getMessage("tame entities"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBreedEntity(EntityBreedEvent e)
	{
		if (!(e.getBreeder() instanceof Player)) return;
		final VirtualIsland vi = this.getIsland(e.getEntity().getLocation());
		if (null == vi || vi.getIsland().getProtection().canInteractEntity((Player) e.getEntity())) return;
		e.getEntity().sendMessage(getMessage("breed entities"));
		e.setCancelled(true);
	}

	@EventHandler
	public void playerTeleport(PlayerTeleportEvent e)
	{
		final VirtualIsland vi = this.getIsland(e.getTo());
		if (null == vi || vi.getIsland().getProtection().canVisit(e.getPlayer())) return;
		e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to visit " + vi.getIsland().getName() + " at this time.");
		e.setCancelled(true);
	}
}
