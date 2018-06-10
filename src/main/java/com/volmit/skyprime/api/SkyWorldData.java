package com.volmit.skyprime.api;

import com.volmit.volume.bukkit.pawn.IPawn;

public class SkyWorldData implements IPawn
{
	private int gridSize;

	public SkyWorldData()
	{
		this.gridSize = 16;
	}

	public int getGridSize()
	{
		return gridSize;
	}

	public void setGridSize(int gridSize)
	{
		this.gridSize = gridSize;
	}
}
