package com.volmit.skyprime.api;

import org.bukkit.World;

import com.volmit.volume.bukkit.pawn.IPawn;

public interface SkyWorld extends IPawn
{
	public World getWorld();

	public SkyWorldConfig getConfig();

	public SkyWorldData getData();

	public void save();

	public void load();

	public int getGridSize();
}
