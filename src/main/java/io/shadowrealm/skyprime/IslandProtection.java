package io.shadowrealm.skyprime;

import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.Visibility;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class IslandProtection
{

	@Getter
	@Setter
	private Island island;

	/**
	 * Indicates public players being able to build
	 *
	 * Allows players to:
	 * - Place and break blocks
	 * - Place and break hanging entities
	 * - Fill or empty buckets
	 */
	@Setter
	private boolean publicBuild = false;

	/**
	 * Indicates a player able to interact with blocks
	 */
	@Setter
	private boolean publicInteractBlock = false;

	/**
	 * Indicates public players able to use blocks: chests, brewing stands, etc
	 */
	@Setter
	private boolean publicUseBlock = false;

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
	private boolean publicInteractEntity = false;

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

	public boolean canBuild(Player p)
	{
		return isAllowed(p) || this.publicBuild;
	}

	public boolean canInteractBlock(Player p)
	{
		return isAllowed(p) || this.publicInteractBlock;
	}

	public boolean canUseBlock(Player p)
	{
		return isAllowed(p) || this.publicUseBlock;
	}

	public boolean canPVP(Player p)
	{
		return isAllowed(p) || this.publicPVP;
	}

	public boolean canKill(Player p)
	{
		return isAllowed(p) || this.publicKill;
	}

	public boolean canInteractEntity(Player p)
	{
		return isAllowed(p) || this.publicInteractEntity;
	}

	public boolean canPickup(Player p)
	{
		return isAllowed(p) || this.island.iscPublicPickup();
	}

	public boolean canVisit(Player player)
	{
		return isAllowed(player) || this.getIsland().getVisibility().equals(Visibility.PUBLIC);
	}

}
