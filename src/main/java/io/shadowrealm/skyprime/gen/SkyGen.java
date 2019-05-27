package io.shadowrealm.skyprime.gen;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class SkyGen extends ChunkGenerator
{
	public SkyGen()
	{

	}

	@Override
	public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
	{
		return new short[world.getMaxHeight() / 16][];
	}
}
