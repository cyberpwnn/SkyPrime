package com.volmit.skyprime.world.gen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.task.S;
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
				GSet<Vector> vv = warpSphere(radiusBlocks);
				vv = dialate(vv);
				vv = flatten(vv, 1);
				vv = round(vv);
				GMap<Vector, Material> mv = materialize(vv);

				new S()
				{
					@SuppressWarnings("deprecation")
					@Override
					public void run()
					{
						for(Vector i : mv.k())
						{
							Material v = mv.get(i);

							if(v.equals(Material.STONE))
							{
								center.clone().add(i).getBlock().setTypeIdAndData(M.r(0.5) ? 1 : 4, (byte) 0, false);
							}

							else if(v.equals(Material.DIRT))
							{
								center.clone().add(i).getBlock().setTypeIdAndData(3, (byte) (M.r(0.5) ? 0 : 1), false);
							}

							else if(v.equals(Material.STONE))
							{
								center.clone().add(i).getBlock().setTypeIdAndData(M.r(0.5) ? 1 : 4, (byte) 0, false);
							}

							else
							{
								center.clone().add(i).getBlock().setTypeIdAndData(v.getId(), (byte) 0, false);
							}
						}

						cb.run(mv.size());
					}
				};
			}
		};
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

	public GMap<Vector, Material> materialize(GSet<Vector> v)
	{
		GMap<Vector, Material> mat = new GMap<Vector, Material>();
		GMap<Vector, Integer> heightmap = getHeightmap(v);

		for(Vector i : v)
		{
			Vector cursor = new Vector(i.getBlockX(), 0, i.getBlockZ());

			if(heightmap.get(cursor) == (int) i.getBlockY())
			{
				mat.put(i, Material.GRASS);
			}

			else if(heightmap.get(cursor) != (int) i.getBlockY() && heightmap.get(cursor) - (int) i.getBlockY() <= 2)
			{
				mat.put(i, Material.DIRT);
			}

			else
			{
				mat.put(i, Material.STONE);
			}
		}

		return mat;
	}

	public GMap<Vector, Integer> getHeightmap(GSet<Vector> v)
	{
		GMap<Vector, Integer> hm = new GMap<Vector, Integer>();

		for(Vector i : v)
		{
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

		for(Vector i : v)
		{
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

		for(Vector i : v)
		{
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

		for(Vector i : v)
		{
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
}
