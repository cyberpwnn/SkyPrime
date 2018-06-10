package com.volmit.skyprime.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.volmit.skyprime.api.SkyChunk;
import com.volmit.skyprime.api.SkyWorld;
import com.volmit.volume.bukkit.util.world.Cuboid;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GListAdapter;

public class SChunk implements SkyChunk
{
	private SkyWorld world;
	private Cuboid cuboid;
	private int x;
	private int z;

	public SChunk(SkyWorld world, int x, int z)
	{
		this.world = world;
		this.x = x;
		this.z = z;
		cuboid = new Cuboid(getUpper4D(), getLower4D());
	}

	@Override
	public SkyWorld getWorld()
	{
		return world;
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getZ()
	{
		return z;
	}

	@Override
	public GList<Entity> getEntities()
	{
		GList<Entity> ent = new GList<Entity>();

		for(Chunk i : getRegion().getChunks())
		{
			for(Entity j : i.getEntities())
			{
				if(getRegion().contains(j.getLocation()))
				{
					ent.add(j);
				}
			}
		}

		return ent;
	}

	@Override
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

	@Override
	public Cuboid getRegion()
	{
		return cuboid;
	}

	@Override
	public Location getUpper4D()
	{
		return getLower4D().clone().add(getGridSize() - 1, 255, getGridSize() - 1);
	}

	@Override
	public Location getLower4D()
	{
		return new Location(getWorld().getWorld(), getGridSize() * getX(), 0, getGridSize() * getZ());
	}

	@Override
	public int getGridSize()
	{
		return getWorld().getGridSize();
	}
}
