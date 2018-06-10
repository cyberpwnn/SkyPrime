package com.volmit.skyprime.api;

import org.bukkit.Location;
import org.bukkit.World;

import com.volmit.volume.bukkit.pawn.IPawn;

public interface SkyWorld extends IPawn
{
	public World getWorld();

	public SkyWorldConfig getConfig();

	public SkyWorldData getData();

	public GridRegion getSpawnGrid();

	public Location getSpawn();

	public void setSpawn(Location spawn);

	public void save();

	public void load();

	public int getGridSize();

	public SkyChunk getChunk(int x, int z);
}
