package com.volmit.skyprime.world;

import org.bukkit.World;

import com.volmit.skyprime.api.SkyChunk;
import com.volmit.skyprime.api.SkyWorld;
import com.volmit.skyprime.api.SkyWorldConfig;
import com.volmit.skyprime.api.SkyWorldData;

public class SWorld implements SkyWorld
{
	private World world;
	private SkyWorldConfig config;
	private SkyWorldData data;

	public SWorld(World world)
	{
		// find / create configs and data
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
		// TODO save
	}

	@Override
	public void load()
	{
		// TODO load
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
}
