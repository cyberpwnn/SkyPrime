package com.volmit.skyprime.world;

import java.util.UUID;

import org.bukkit.Location;

import com.volmit.skyprime.api.GridRegion;
import com.volmit.skyprime.api.Island;
import com.volmit.skyprime.api.IslandDifficulty;
import com.volmit.skyprime.api.IslandRole;
import com.volmit.skyprime.api.SkyChunk;
import com.volmit.skyprime.api.SkyPlayer;
import com.volmit.skyprime.api.SkyWorld;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

public class SIsland implements Island
{
	private UUID id;
	private GMap<SkyPlayer, IslandRole> roles;
	private GridRegion region;
	private SkyWorld world;
	private IslandDifficulty difficulty;
	private Location spawn;

	@Override
	public UUID getID()
	{
		return id;
	}

	@Override
	public GList<SkyPlayer> getOwners()
	{
		GList<SkyPlayer> s = new GList<SkyPlayer>();

		for(SkyPlayer i : getMembers().k())
		{
			if(isRole(i, IslandRole.MEMBER))
			{
				s.add(i);
			}
		}

		return s;
	}

	@Override
	public boolean hasOwners()
	{
		return !getOwners().isEmpty();
	}

	@Override
	public IslandDifficulty getDifficulty()
	{
		return difficulty;
	}

	@Override
	public void setDifficulty(IslandDifficulty difficulty)
	{
		this.difficulty = difficulty;
	}

	@Override
	public void addMember(SkyPlayer player, IslandRole role)
	{
		if(role.equals(IslandRole.VISITOR))
		{
			return;
		}

		roles.put(player, role);
	}

	@Override
	public boolean isRole(SkyPlayer player, IslandRole role)
	{
		if(roles.containsKey(player))
		{
			return getRole(player).ordinal() >= role.ordinal();
		}

		else if(role.equals(IslandRole.VISITOR))
		{
			return true;
		}

		return false;
	}

	@Override
	public void setRole(SkyPlayer player, IslandRole role)
	{
		if(roles.containsKey(player))
		{
			if(role.equals(IslandRole.VISITOR))
			{
				removeMember(player);
			}

			else
			{
				roles.put(player, role);
			}
		}
	}

	@Override
	public void removeMember(SkyPlayer player)
	{
		roles.remove(player);
	}

	@Override
	public IslandRole getRole(SkyPlayer player)
	{
		return roles.containsKey(player) ? roles.get(player) : IslandRole.VISITOR;
	}

	@Override
	public GMap<SkyPlayer, IslandRole> getMembers()
	{
		return roles;
	}

	@Override
	public SkyWorld getWorld()
	{
		return world;
	}

	@Override
	public SkyChunk getCenterChunk()
	{
		return region.getCenterChunk();
	}

	@Override
	public Location getSpawn()
	{
		return spawn;
	}

	@Override
	public void setSpawn(Location l)
	{
		spawn = l;
	}

	@Override
	public GridRegion getGridRegion()
	{
		return region;
	}
}
