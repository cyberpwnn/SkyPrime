package com.volmit.skyprime.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.volmit.volume.bukkit.util.world.Cuboid;
import com.volmit.volume.lang.collections.GList;

public interface SkyChunk
{
	public SkyWorld getWorld();

	public GList<Chunk> getChunks();

	public boolean contains(Location l);

	public boolean contains(Entity e);

	public boolean contains(Block b);

	public int getX();

	public int getZ();

	public GList<Entity> getEntities();

	public GList<Player> getPlayers();

	public Cuboid getRegion();

	public Location getUpper4D();

	public Location getLower4D();

	public int getGridSize();
}
