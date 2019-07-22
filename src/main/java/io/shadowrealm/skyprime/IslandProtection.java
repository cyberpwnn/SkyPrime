package io.shadowrealm.skyprime;

import io.shadowrealm.skyprime.storage.Island;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class IslandProtection
{

	@Getter
	@Setter
	private Island island;

	/**
	 * Indicates a public player being able to break or place blocks
	 */
	@Setter
	private boolean publicBreak = false;

	/**
	 * Indicates a player able to interact with blocks
	 */
	@Setter
	private boolean publicInteract = false;

	/**
	 * Indicates public players able to use blocks: chests, brewing stands, etc
	 */
	@Setter
	private boolean publicUse = false;

	/**
	 * Indicates public players able to PVP
	 */
	@Setter
	private boolean publicPVP = false;

	/**
	 * Indicates public players able to kill entities
	 */
	@Setter
	private boolean publicKill = false;

	/**
	 * Indicates public players able to damage entities
	 */
	@Setter
	private boolean publicDamage = false;

	public IslandProtection(Island i)
	{
		this.island = i;
	}

	public boolean isAdmin(Player p)
	{
		return p.isOp() || SkyPrime.perm.admin.bypass.has(p);
	}

	public boolean isOwner(Player p)
	{
		return island.getOwner().equals(p.getUniqueId());
	}

	public boolean isMember(Player p)
	{
		return island.getMembers().contains(p.getUniqueId());
	}

	public boolean isAllowed(Player p)
	{
		return isMember(p) || isOwner(p) || isAdmin(p);
	}

	public boolean canBreak(Player p)
	{
		return isAllowed(p) || this.publicBreak;
	}

	public boolean canInterract(Player p)
	{
		return isAllowed(p) || this.publicInteract;
	}

	public boolean canUse(Player p)
	{
		return isAllowed(p) || this.publicUse;
	}

	public boolean canPVP(Player p)
	{
		return isAllowed(p) || this.publicPVP;
	}

	public boolean canKill(Player p)
	{
		return isAllowed(p) || this.publicKill;
	}

	public boolean canDamage(Player p)
	{
		return isAllowed(p) || this.publicDamage;
	}

	public boolean canPickup(Player p)
	{
		return isAllowed(p) || this.island.iscPublicPickup();
	}

}
