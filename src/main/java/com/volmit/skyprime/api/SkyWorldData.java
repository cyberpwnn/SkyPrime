package com.volmit.skyprime.api;

import java.io.File;

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

	public void save(File file)
	{
		// TODO Auto-generated method stub

	}

	public void load(File file)
	{
		// TODO Auto-generated method stub

	}
}
