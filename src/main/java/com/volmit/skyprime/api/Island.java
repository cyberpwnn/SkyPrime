package com.volmit.skyprime.api;

import java.util.UUID;

import org.bukkit.Location;

import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

public interface Island
{
	public UUID getID();

	public GList<SkyPlayer> getOwners();

	public boolean hasOwners();

	public void addMember(SkyPlayer player, IslandRole role);

	public boolean isRole(SkyPlayer player, IslandRole role);

	public void setRole(SkyPlayer player, IslandRole role);

	public void removeMember(SkyPlayer player);

	public IslandRole getRole(SkyPlayer player);

	public GMap<SkyPlayer, IslandRole> getMembers();

	public SkyWorld getWorld();

	public SkyChunk getCenterChunk();

	public Location getSpawn();

	public void setSpawn(Location l);

	public GridRegion getGridRegion();
}
