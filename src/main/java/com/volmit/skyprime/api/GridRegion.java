package com.volmit.skyprime.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.volmit.volume.bukkit.util.world.Cuboid;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GListAdapter;

public class GridRegion
{
	private SkyChunk min;
	private SkyChunk max;
	private GList<SkyChunk> skyChunks;
	private GList<Chunk> chunks;

	public GridRegion(SkyChunk min, SkyChunk max)
	{
		this.max = max;
		this.min = min;
	}

	public Cuboid getRegion()
	{
		return new Cuboid(min.getLower4D(), max.getUpper4D());
	}

	public GList<SkyChunk> getSkyChunks()
	{
		if(skyChunks != null)
		{
			return skyChunks;
		}

		SkyWorld world = max.getWorld();

		for(int i = min.getX(); i <= max.getX(); i++)
		{
			for(int j = min.getZ(); j <= max.getZ(); j++)
			{
				skyChunks.add(world.getChunk(i, j));
			}
		}

		return skyChunks;
	}

	public GList<Chunk> getChunks()
	{
		if(chunks != null)
		{
			return chunks;
		}

		for(SkyChunk i : getSkyChunks())
		{
			for(Chunk j : i.getChunks())
			{
				if(!chunks.contains(j))
				{
					chunks.add(j);
				}
			}
		}

		return chunks;
	}

	public GList<Player> getPlayers()
	{
		return (GList<Player>) new GListAdapter<Entity, Player>()
		{
			@Override
			public Player onAdapt(Entity from)
			{
				return from instanceof Player ? (Player) from : null;
			}
		}.adapt(getEntities());
	}

	public GList<Entity> getEntities()
	{
		GList<Entity> e = new GList<Entity>();

		for(SkyChunk i : getSkyChunks())
		{
			e.addAll(i.getEntities());
		}

		return e;
	}

	public SkyChunk getCenterChunk()
	{
		return min.getWorld().getChunk(max.getX() - (min.getX() / 2), max.getZ() - (min.getZ() / 2));
	}
}
