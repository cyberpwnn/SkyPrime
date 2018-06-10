package com.volmit.skyprime.world.gen;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import com.volmit.volume.math.M;

public class SkyGen extends ChunkGenerator
{
	private int grid;
	private int dist;

	public SkyGen(int grid, int spawnRadius)
	{
		this.grid = grid;
		dist = (spawnRadius / grid) + 1;
		dist = dist % 2 == 0 ? dist + 1 : dist;
	}

	@SuppressWarnings("deprecation")
	@Override
	public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
	{
		int maxY = world.getMaxHeight();
		short[][] result = new short[maxY / 16][];

		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 16; j++)
			{
				int xx = i + (x * 16);
				int zz = j + (z * 16);
				int sx = xx / grid;
				int sz = zz / grid;

				if(sx <= dist && sx >= -dist && sz <= dist && sz >= -dist)
				{
					if(M.r(0.5))
					{
						setBlock(result, i, 1, j, (short) 1);
					}

					else
					{
						setBlock(result, i, 1, j, (short) 4);
					}
				}

				if((xx == 0 || zz == 0) || (xx == -1 || zz == -1) || ((xx + 1) / grid != sx || (zz + 1) / grid != sz) || ((xx - 1) / grid != sx || (zz - 1) / grid != sz))
				{
					setBlock(result, i, 1, j, (short) Material.SEA_LANTERN.getId());
				}
			}
		}

		return result;
	}

	private void setBlock(short[][] result, int x, int y, int z, short blkid)
	{
		if(result[y >> 4] == null)
		{
			result[y >> 4] = new short[4096];
		}

		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
	}
}
