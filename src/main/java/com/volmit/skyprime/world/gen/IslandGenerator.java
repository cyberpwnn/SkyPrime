package com.volmit.skyprime.world.gen;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Leaves;
import org.bukkit.material.Stairs;
import org.bukkit.material.Wood;
import org.bukkit.util.Vector;

import com.volmit.volume.bukkit.U;
import com.volmit.volume.bukkit.nms.NMSSVC;
import com.volmit.volume.bukkit.nms.adapter.AbstractChunk;
import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.world.MaterialBlock;
import com.volmit.volume.lang.collections.Callback;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.collections.GSet;
import com.volmit.volume.math.Average;
import com.volmit.volume.math.M;
import com.volmit.volume.math.noise.SimplexOctaveGenerator;

public class IslandGenerator
{
	private int octaves;
	private Location center;
	private long seed;
	private int radiusBlocks;
	private double divisor;
	private double dimension;
	private double noise;
	private double amplifier;
	private double frequency;
	private double squashTop;
	private double squashBottom;
	private double total;
	private double at;
	private String status;

	public IslandGenerator(Location center, long seed)
	{
		this.center = center;
		this.seed = seed;
		divisor = 100D;
		noise = 5D;
		radiusBlocks = 7;
		dimension = 0.5;
		amplifier = 0.5;
		frequency = 0.5;
		octaves = 4;
		squashTop = 2.5;
		squashBottom = 17;
		total = 0;
		at = 0;
		status = "Idle";
	}

	public double getProgress()
	{
		return at / total;
	}

	public IslandGenerator(Location center)
	{
		this(center, (long) (Math.random() * Long.MAX_VALUE));
	}

	public void generate(Callback<Integer> cb)
	{
		new A()
		{
			@Override
			public void run()
			{
				rset("Modeling");
				GSet<Vector> vv = warpSphere(radiusBlocks);
				rset("Dialating");
				vv = dialate(vv);
				rset("Squashing");
				vv = flatten(vv, 1);
				rset("Rounding");
				vv = round(vv);
				rset("Realizing");
				GMap<Vector, MaterialBlock> mv = materialize(vv);
				GSet<Chunk> chk = new GSet<Chunk>();
				rset("Building");
				total += mv.size();

				for(Vector i : mv.k())
				{
					try
					{
						at++;
						chk.add(center.clone().add(i).getChunk());
						U.getService(NMSSVC.class).setBlock(center.clone().add(i), mv.get(i));
					}

					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}

				new S()
				{
					@Override
					public void run()
					{
						int ii = 0;

						for(Chunk i : chk)
						{
							new S(ii / 4)
							{
								@Override
								public void run()
								{
									U.getService(NMSSVC.class).sendChunkMap(new AbstractChunk(i), i);
								}
							};

							ii++;
						}

						cb.run(mv.size());
					}
				};
			}

		};
	}

	private void rset(String string)
	{
		at = 0;
		total = 0;
		status = string;
	}

	public GList<Vector> reorder(GList<Vector> k)
	{
		GList<Vector> gg = new GList<Vector>();

		while(!k.isEmpty())
		{
			Vector low = null;
			int lst = Integer.MAX_VALUE;

			for(Vector i : k)
			{
				if(i.getBlockY() < lst)
				{
					lst = i.getBlockY();
					low = i;
				}
			}

			k.remove(low);
			gg.add(low);
		}

		return gg;
	}

	public GSet<Vector> blend(GSet<Vector> in)
	{
		return in;
	}

	@SuppressWarnings("deprecation")
	public GMap<Vector, MaterialBlock> materialize(GSet<Vector> v)
	{
		GMap<Vector, MaterialBlock> mat = new GMap<Vector, MaterialBlock>();
		GMap<Vector, Integer> heightmap = getHeightmap(v);
		total += v.size();
		boolean treeyet = false;

		Average ax = new Average(v.size());
		Average az = new Average(v.size());

		for(Vector i : v)
		{
			ax.put(i.getX());
			az.put(i.getZ());
		}

		for(Vector i : v)
		{
			at++;

			Vector cursor = new Vector(i.getBlockX(), 0, i.getBlockZ());
			int shift = (int) ((int) (Math.abs(i.getX() - ax.getAverage())) + Math.abs(i.getZ() - az.getAverage()));

			if(heightmap.get(cursor) == (int) i.getBlockY())
			{
				mat.put(i, new MaterialBlock(Material.GRASS));

				if(M.r(0.17) && !v.contains(i.clone().add(new Vector(0, 1, 0))))
				{
					if(M.r(0.05))
					{
						mat.put(i.clone().add(new Vector(0, 1, 0)), new MaterialBlock(Material.RED_ROSE, (byte) M.rand(0, 7)));
					}

					else if(M.r(0.45))
					{
						mat.put(i.clone().add(new Vector(0, 1, 0)), new MaterialBlock(Material.LONG_GRASS, (byte) 2));
					}

					else
					{
						mat.put(i.clone().add(new Vector(0, 1, 0)), new MaterialBlock(Material.LONG_GRASS, (byte) 1));
					}
				}

				else if(!treeyet && shift < 5 && !v.contains(i.clone().add(new Vector(0, 1, 0))))
				{
					treeyet = true;
					Wood w = new Wood();
					TreeSpecies ttx = TreeSpecies.values()[M.rand(0, TreeSpecies.values().length - 1)];

					try
					{
						w = new Wood(Material.LOG, ttx);
					}

					catch(IllegalArgumentException e)
					{
						w = new Wood(Material.LOG_2, ttx);
					}

					int h = 8;

					for(int j = 0; j < h; j++)
					{
						mat.put(i.clone().add(new Vector(0, j, 0)), new MaterialBlock(w.getItemType(), (byte) w.getData()));
					}

					GSet<Vector> vl = warpSphereTree(4);
					vl = flatten(vl, 1);

					for(Vector j : vl)
					{
						Vector jj = j.clone().add(i).add(new Vector(0, h, 0));
						if(!mat.containsKey(jj))
						{
							Leaves l = new Leaves();
							TreeSpecies tt = TreeSpecies.values()[M.rand(0, TreeSpecies.values().length - 1)];

							try
							{
								l = new Leaves(Material.LEAVES, tt);
							}

							catch(IllegalArgumentException e)
							{
								l = new Leaves(Material.LEAVES_2, tt);
							}

							mat.put(jj, new MaterialBlock(l.getItemType(), l.getData()));
						}
					}
				}
			}

			else if(heightmap.get(cursor) != (int) i.getBlockY() && heightmap.get(cursor) - (int) i.getBlockY() <= 2)
			{
				mat.put(i, new MaterialBlock(Material.DIRT, (byte) (M.r(0.35) ? 1 : 0)));
			}

			else if(heightmap.get(cursor) > (int) i.getBlockY() && !v.contains(i.clone().add(new Vector(0, 1, 0))))
			{
				mat.put(i, new MaterialBlock(Material.GRAVEL));
			}

			else if(heightmap.get(cursor) > (int) i.getBlockY() && !v.contains(i.clone().add(new Vector(0, -1, 0))))
			{
				if(M.r(0.25))
				{
					mat.put(i, new MaterialBlock(Material.COBBLE_WALL, (byte) (M.r(0.25) ? 1 : 0)));
				}

				else
				{
					Stairs s = new Stairs(Material.COBBLESTONE_STAIRS);
					s.setInverted(true);
					s.setFacingDirection(M.r(0.5) ? M.r(0.5) ? BlockFace.NORTH : BlockFace.EAST : M.r(0.5) ? BlockFace.SOUTH : BlockFace.WEST);
					mat.put(i, new MaterialBlock(Material.COBBLESTONE_STAIRS, (byte) s.getData()));
				}
			}

			else
			{
				mat.put(i, new MaterialBlock(M.r(0.15) ? Material.MOSSY_COBBLESTONE : M.r(0.35) ? Material.COBBLESTONE : Material.STONE));
			}
		}

		return mat;
	}

	public GMap<Vector, Integer> getHeightmap(GSet<Vector> v)
	{
		GMap<Vector, Integer> hm = new GMap<Vector, Integer>();
		total += v.size();
		for(Vector i : v)
		{
			at++;
			Vector c = new Vector(i.getBlockX(), 0, i.getBlockZ());

			if(!hm.containsKey(c))
			{
				hm.put(c, Integer.MIN_VALUE);
			}

			if((int) i.getY() > hm.get(c))
			{
				hm.put(c, i.getBlockY());
			}
		}

		return hm;
	}

	public GSet<Vector> dialate(GSet<Vector> v)
	{
		GSet<Vector> vv = new GSet<Vector>();
		total += v.size();
		for(Vector i : v)
		{
			at++;
			vv.addAll(generateSphere(i.getBlockX(), i.getBlockY(), i.getBlockZ(), 2));
		}

		return vv;
	}

	public GSet<Vector> flatten(GSet<Vector> v, int factor)
	{
		GSet<Vector> roll = new GSet<Vector>();
		roll.addAll(v);

		for(int i = 0; i < factor; i++)
		{
			roll = flatten(roll);
		}

		return roll;
	}

	public GSet<Vector> round(GSet<Vector> v)
	{
		GSet<Vector> roll = new GSet<Vector>();
		total += v.size();
		for(Vector i : v)
		{
			at++;
			roll.add(round(i));
		}

		return roll;
	}

	public GSet<Vector> flatten(GSet<Vector> v)
	{
		GSet<Vector> vv = new GSet<Vector>();
		Average a = new Average(v.size());

		for(Vector i : v)
		{
			a.put(i.getBlockY());
		}

		total += v.size();
		for(Vector i : v)
		{
			at++;
			if(i.getY() > a.getAverage())
			{
				double amt = (i.getY() - a.getAverage()) / squashTop;
				vv.add(i.clone().subtract(new Vector(0, amt, 0)));
			}

			else if(i.getY() < a.getAverage())
			{
				double amt = (a.getAverage() - i.getY()) / squashBottom;
				vv.add(i.clone().add(new Vector(0, amt, 0)));
			}

			else
			{
				vv.add(i);
			}
		}

		return vv;
	}

	public GSet<Vector> warpSphere(int radius)
	{
		GSet<Vector> vm = new GSet<Vector>();
		SimplexOctaveGenerator sog = new SimplexOctaveGenerator(seed, octaves);
		sog.setScale(0.01 * noise);
		GSet<Vector> v = new GSet<Vector>();
		v.addAll(generateSphere(radius));
		total += v.size();

		for(Vector i : new GList<Vector>(v))
		{
			double n = (sog.noise(i.getX(), i.getY(), i.getZ(), frequency, amplifier, true) + 1D) / 2D;

			if(n > dimension)
			{
				int x = i.getBlockX();
				int y = i.getBlockY();
				int z = i.getBlockZ();
				int nx = (int) (sog.noise(z + y, frequency, amplifier, true) * noise);
				int ny = (int) (sog.noise(x + z, frequency, amplifier, true) * noise);
				int nz = (int) (sog.noise(y + x, frequency, amplifier, true) * noise);
				Vector nv = new Vector(nx, ny, nz).clone().add(i);

				for(double j = 1; j > 0.01; j -= 1D / noise)
				{
					vm.add(nv.clone().multiply(j));
				}
			}

			at++;
		}

		return vm;
	}

	public GSet<Vector> warpSphereTree(int radius)
	{
		GSet<Vector> vm = new GSet<Vector>();
		SimplexOctaveGenerator sog = new SimplexOctaveGenerator(seed, octaves);
		sog.setScale(0.1);
		GSet<Vector> v = new GSet<Vector>();
		v.addAll(generateSphere(radius));
		total += v.size();

		for(Vector i : new GList<Vector>(v))
		{
			double n = (sog.noise(i.getX(), i.getY(), i.getZ(), frequency, amplifier, true) + 1D) / 2D;

			if(n > 0.45)
			{
				int x = i.getBlockX();
				int y = i.getBlockY();
				int z = i.getBlockZ();
				int nx = (int) (sog.noise(z + y, frequency, amplifier, true) * 2);
				int ny = (int) (sog.noise(x + z, frequency, amplifier, true) * 2);
				int nz = (int) (sog.noise(y + x, frequency, amplifier, true) * 2);
				Vector nv = new Vector(nx, ny, nz).clone().add(i);

				for(double j = 1; j > 0.01; j -= 1D / noise)
				{
					vm.add(nv.clone().multiply(j));
				}
			}

			at++;
		}

		return vm;
	}

	public GSet<Vector> warpSphereWater(int radius)
	{
		GSet<Vector> vm = new GSet<Vector>();
		SimplexOctaveGenerator sog = new SimplexOctaveGenerator(seed, octaves);
		sog.setScale(0.1);
		GSet<Vector> v = new GSet<Vector>();
		v.addAll(generateSphere(radius));
		total += v.size();

		for(Vector i : new GList<Vector>(v))
		{
			double n = (sog.noise(i.getX(), i.getY(), i.getZ(), frequency, amplifier, true) + 1D) / 2D;

			if(n > 0.15)
			{
				int x = i.getBlockX();
				int y = i.getBlockY();
				int z = i.getBlockZ();
				int nx = (int) (sog.noise(z + y, frequency, amplifier, true) * 2);
				int ny = (int) (sog.noise(x + z, frequency, amplifier, true) * 2);
				int nz = (int) (sog.noise(y + x, frequency, amplifier, true) * 2);
				Vector nv = new Vector(nx, ny, nz).clone().add(i);

				for(double j = 1; j > 0.01; j -= 1D / noise)
				{
					vm.add(nv.clone().multiply(j));
				}
			}

			at++;
		}

		return vm;
	}

	public Vector round(Vector v)
	{
		return new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}

	public GSet<Vector> generateSphere(double radius)
	{
		return generateSphere(0, 0, 0, radius);
	}

	public GSet<Vector> generateSphere(int sx, int sy, int sz, double radius)
	{
		GSet<Vector> v = new GSet<Vector>();
		Vector pos = new Vector(sx, sy, sz);
		radius += 0.5D;
		double radiusSq = radius * radius;
		int ceilRadius = (int) Math.ceil(radius);

		for(int x = 0; x <= ceilRadius; x++)
		{
			for(int y = 0; y <= ceilRadius; y++)
			{
				for(int z = 0; z <= ceilRadius; z++)
				{
					double dSq = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);

					if(dSq > radiusSq)
					{
						continue;
					}

					v.add(pos.clone().add(new Vector(x, y, z)));
					v.add(pos.clone().add(new Vector(-x, y, z)));
					v.add(pos.clone().add(new Vector(x, -y, z)));
					v.add(pos.clone().add(new Vector(x, y, -z)));
					v.add(pos.clone().add(new Vector(-x, -y, z)));
					v.add(pos.clone().add(new Vector(x, -y, -z)));
					v.add(pos.clone().add(new Vector(-x, y, -z)));
					v.add(pos.clone().add(new Vector(-x, -y, -z)));
				}
			}
		}

		return v;
	}

	public double getDimension()
	{
		return dimension;
	}

	public void setDimension(double dimension)
	{
		this.dimension = dimension;
	}

	public double getNoise()
	{
		return noise;
	}

	public void setNoise(double noise)
	{
		this.noise = noise;
	}

	public int getOctaves()
	{
		return octaves;
	}

	public void setOctaves(int octaves)
	{
		this.octaves = octaves;
	}

	public Location getCenter()
	{
		return center;
	}

	public void setCenter(Location center)
	{
		this.center = center;
	}

	public long getSeed()
	{
		return seed;
	}

	public void setSeed(long seed)
	{
		this.seed = seed;
	}

	public int getRadiusBlocks()
	{
		return radiusBlocks;
	}

	public void setRadiusBlocks(int radiusBlocks)
	{
		this.radiusBlocks = radiusBlocks;
	}

	public double getDivisor()
	{
		return divisor;
	}

	public void setDivisor(double divisor)
	{
		this.divisor = divisor;
	}

	public double getAmplifier()
	{
		return amplifier;
	}

	public void setAmplifier(double amplifier)
	{
		this.amplifier = amplifier;
	}

	public double getFrequency()
	{
		return frequency;
	}

	public void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}

	public double getSquashTop()
	{
		return squashTop;
	}

	public void setSquashTop(double squashTop)
	{
		this.squashTop = squashTop;
	}

	public double getSquashBottom()
	{
		return squashBottom;
	}

	public void setSquashBottom(double squashBottom)
	{
		this.squashBottom = squashBottom;
	}

	public String setStatus()
	{
		return status;
	}
}
