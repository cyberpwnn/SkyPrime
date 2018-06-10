package com.volmit.skyprime.world;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

import com.volmit.skyprime.api.GridRegion;
import com.volmit.skyprime.api.SkyChunk;
import com.volmit.skyprime.api.SkyWorld;
import com.volmit.skyprime.api.SkyWorldConfig;
import com.volmit.skyprime.api.SkyWorldData;

public class SWorld implements SkyWorld
{
	private World world;
	private SkyWorldConfig config;
	private SkyWorldData data;
	private GridRegion spawnGrid;
	private Location spawn;

	public SWorld(World world)
	{
		config = new SkyWorldConfig();
		data = new SkyWorldData();
		this.world = world;
		int dist = (32 / getGridSize()) + 1;
		dist = dist % 2 == 0 ? dist + 1 : dist;
		spawnGrid = new GridRegion(getChunk(-dist, -dist), getChunk(dist, dist));
		setSpawn(new Location(world, 0, 42, 0));
	}

	@Override
	public World getWorld()
	{
		return world;
	}

	@Override
	public SkyWorldConfig getConfig()
	{
		return config;
	}

	@Override
	public SkyWorldData getData()
	{
		return data;
	}

	@Override
	public void save()
	{
		getConfig().save(new File(getWorld().getWorldFolder(), "sky.yml"));
		getData().save(new File(getWorld().getWorldFolder(), "sky"));
	}

	@Override
	public void load()
	{
		getConfig().load(new File(getWorld().getWorldFolder(), "sky.yml"));
		getData().load(new File(getWorld().getWorldFolder(), "sky"));
	}

	@Override
	public int getGridSize()
	{
		return getData().getGridSize();
	}

	@Override
	public SkyChunk getChunk(int x, int z)
	{
		return new SChunk(this, x, z);
	}

	@Override
	public GridRegion getSpawnGrid()
	{
		return spawnGrid;
	}

	@Override
	public Location getSpawn()
	{
		return spawn.clone();
	}

	@Override
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
	}
}
