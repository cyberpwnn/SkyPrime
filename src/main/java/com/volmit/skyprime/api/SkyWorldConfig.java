package com.volmit.skyprime.api;

import java.io.File;

import com.volmit.volume.bukkit.pawn.Comment;
import com.volmit.volume.bukkit.pawn.IPawn;
import com.volmit.volume.bukkit.pawn.Node;

public class SkyWorldConfig implements IPawn
{
	@Node("skyworld.display-name")
	@Comment("The display name of this skyworld")
	public String displayName = "&bSky &7World";

	@Node("skyworld.grid.island-radius")
	@Comment("The radius (in grid chunks) from the center grid players can build to")
	public int islandSize = 2;

	@Node("skyworld.grid.padding")
	@Comment("The seperation between island grids. I.e. How far apart would two edges of island regions be.")
	public int gridPadding = 1;

	@Node("skyworld.island.scale")
	@Comment("The scale of the island generator")
	public int islandScale = 8;

	@Node("skyworld.island.noise-magnitude")
	@Comment("The noise magnitude. The higher, the more violent an island can look. Set this to 0 for no noise.")
	public double noiseMagnitude = 5.13;

	@Node("skyworld.island.octaves")
	@Comment("How many simplex noise octaves to use during generation.")
	public int octaves = 4;

	@Node("skyworld.island.dimension-crossover")
	@Comment("How much 'mass' should be used in a sphere based on noise. Set to 0 for all mass (sphere), anything higher than 1 will result in nothing generated (no mass).")
	public double dimension = 0.5;

	public void save(File file)
	{
		// TODO Auto-generated method stub

	}

	public void load(File file)
	{
		// TODO Auto-generated method stub

	}
}
