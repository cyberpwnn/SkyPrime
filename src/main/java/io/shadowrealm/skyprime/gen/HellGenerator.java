package io.shadowrealm.skyprime.gen;

import mortar.api.world.MaterialBlock;
import mortar.compute.math.Average;
import mortar.compute.math.M;
import mortar.lang.collection.GMap;
import mortar.lang.collection.GSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;
import org.bukkit.util.Vector;

public class HellGenerator extends IslandGenerator
{
	public HellGenerator(Location center, long seed)
	{
		super(center, seed);
		this.setSquashTop(1.80);
		this.setSquashBottom(1.15);
	}

	public GMap<Vector, MaterialBlock> materialize(GSet<Vector> v)
	{
		GMap<Vector, MaterialBlock> mat = new GMap<Vector, MaterialBlock>();
		GMap<Vector, Integer> heightmap = getHeightmap(v);
		total += v.size() * 3;

		Average ax = new Average(8);
		Average az = new Average(8);

		for(Vector i : v)
		{
			if(i.getX() > furthest)
			{
				furthest = i.getX();
			}

			if(i.getZ() > furthest)
			{
				furthest = i.getZ();
			}

			ax.put(i.getX());
			az.put(i.getZ());
			at++;
		}

		for(Vector i : v)
		{
			at++;

			Vector cursor = new Vector(i.getBlockX(), 0, i.getBlockZ());

			if(heightmap.get(cursor) == (int) i.getBlockY())
			{
				validSpawns.add(center.clone().add(i));

				if(validSpawns.size() > 16)
				{
					validSpawns.popRandom();
				}

				if(M.r(0.75))
				{
					mat.put(i, new MaterialBlock(Material.NETHERRACK));
				}

				else if(M.r(0.40))
				{
					mat.put(i, new MaterialBlock(Material.SOUL_SAND, (byte) (M.r(0.45) ? 2 : 1)));
				}

				else
				{
					mat.put(i, new MaterialBlock(Material.GRAVEL, (byte) (M.r(0.45) ? 0 : 1)));
				}

			}

			else if(heightmap.get(cursor) != (int) i.getBlockY() && heightmap.get(cursor) - (int) i.getBlockY() <= 2)
			{
				mat.put(i, new MaterialBlock(M.r(0.01) ? Material.LAVA : Material.NETHERRACK, (byte) (M.r(0.5) ? 1 : M.r(0.25) ? 2 : 0)));
			}

			else if(heightmap.get(cursor) > (int) i.getBlockY() && !v.contains(i.clone().add(new Vector(0, 1, 0))))
			{
				mat.put(i, new MaterialBlock(Material.GRAVEL));
			}

			else if(heightmap.get(cursor) > (int) i.getBlockY() && !v.contains(i.clone().add(new Vector(0, -1, 0))))
			{
				if(M.r(0.25))
				{
					mat.put(i, new MaterialBlock(Material.WEB, (byte) (M.r(0.25) ? 1 : 0)));
				}

				else
				{
					Stairs s = new Stairs(Material.NETHER_BRICK_STAIRS);
					s.setInverted(true);
					s.setFacingDirection(M.r(0.5) ? M.r(0.5) ? BlockFace.NORTH : BlockFace.EAST : M.r(0.5) ? BlockFace.SOUTH : BlockFace.WEST);
					mat.put(i, new MaterialBlock(Material.NETHER_BRICK_STAIRS, (byte) s.getData()));
				}
			}

			else
			{
				mat.put(i, new MaterialBlock(M.r(0.01) ? Material.QUARTZ_ORE : M.r(0.003) ? Material.COAL_ORE : Material.NETHERRACK));
			}
		}

		return mat;
	}
}
