package com.volmit.skyprime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.volmit.skyprime.gen.IslandGenerator;
import com.volmit.skyprime.gen.SkyGen;
import com.volmit.skyprime.storage.Island;
import com.volmit.skyprime.storage.StorageEngine;
import com.volmit.volume.bukkit.task.SR;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.Callback;
import com.volmit.volume.lang.collections.FinalInteger;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.io.VIO;
import com.volmit.volume.math.Profiler;

public class SkyMaster
{
	public static int maxSize = 80;
	private static StorageEngine engine;
	private static GMap<Island, VirtualIsland> virtualIslands = new GMap<>();
	private static FileConfiguration fc;
	private static GMap<String, Integer> sizemap = new GMap<>();

	public static void loadConfig()
	{
		File f = SkyPrime.vpi.getDataFile("config.yml");

		if(!f.exists())
		{
			f.getParentFile().mkdirs();
			FileConfiguration fc = new YamlConfiguration();
			fc.set("sizes.default", 80);
			fc.set("sizes.ranks", new GList<String>().qadd("a=128").qadd("b=192").qadd("c=256").qadd("d=320").qadd("e=384"));
			try
			{
				fc.save(f);
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		FileConfiguration fc = new YamlConfiguration();

		try
		{
			fc.load(f);
			maxSize = fc.getInt("sizes.default");
			sizemap.clear();

			for(String i : fc.getStringList("sizes.ranks"))
			{
				if(i.contains("="))
				{
					String m = i.split("=")[0].trim();
					int v = Integer.valueOf(i.split("=")[1].trim());
					sizemap.put(m, v);
				}
			}
		}

		catch(IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	public static int getSizeFor(Player p)
	{
		int max = maxSize;

		for(String i : sizemap.k())
		{
			if(p.hasPermission("sky.size." + i.toLowerCase()) && sizemap.get(i) > max)
			{
				max = sizemap.get(i);
			}
		}

		return max;
	}

	public static void setStorageEngine(StorageEngine e)
	{
		engine = e;
		File f = SkyPrime.vpi.getDataFile("worth.yml");
		loadConfig();

		try
		{
			if(!f.exists())
			{
				f.getParentFile().mkdirs();
				InputStream in = SkyPrime.class.getResourceAsStream("/worth.yml");
				FileOutputStream fos = new FileOutputStream(f);
				VIO.fullTransfer(in, fos, 8192);
				in.close();
				fos.close();
			}

			fc = new YamlConfiguration();
			fc.load(f);
		}

		catch(Throwable ex)
		{
			ex.printStackTrace();
		}
	}

	public static void tick()
	{
		for(VirtualIsland i : virtualIslands.v())
		{
			i.tick();
		}
	}

	public static void deleteIslands()
	{
		File f = new File("skydata/deletions");

		if(f.exists())
		{
			for(File i : f.listFiles())
			{
				try
				{
					UUID id = UUID.fromString(i.getName());
					File ff = new File(worldName(id));
					VIO.delete(ff);

					if(!ff.exists())
					{
						i.delete();
					}
				}

				catch(Throwable e)
				{

				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void unloadWorld(World world, boolean save)
	{
		World safe = Bukkit.getWorld("world");

		if(world == null || safe == null)
		{
			System.out.println("Cannot unload a null world (or safe world 'world' is null)");
			return;
		}

		for(Player i : world.getPlayers())
		{
			i.sendMessage("We have teleported you to saftey. The dimension you were in was colapsing!");
			i.teleport(safe.getSpawnLocation());
		}

		for(Chunk i : world.getLoadedChunks())
		{
			i.unload(true, true);
		}

		System.out.println("Unloading Dimension: " + world.getName());
		Bukkit.unloadWorld(world, true);
	}

	private static void loadWorld(Island is)
	{
		System.out.println("world " + worldName(is.getId()));
		WorldCreator ww = new WorldCreator(worldName(is.getId()));
		ww.generator(new SkyGen());
		World w = Bukkit.createWorld(ww);

		if(w != null)
		{
			System.out.println("Loaded Dimension: " + w.getName());
			virtualIslands.put(is, new VirtualIsland(w, is));
		}

		else
		{
			System.out.println("Null world");
		}
	}

	public static boolean hasIsland(Player p)
	{
		return engine.hasIslandByOwner(p.getUniqueId());
	}

	public static boolean hasIslandLoaded(Player p)
	{
		return getIsland(p) != null;
	}

	public static boolean hasIsland(UUID p)
	{
		return engine.hasIslandById(p);
	}

	public static Island getIslandConfig(Player p)
	{
		return engine.getIslandByOwner(p.getUniqueId());
	}

	public static Island getIslandConfig(UUID id)
	{
		return engine.getIslandById(id);
	}

	public static VirtualIsland getIsland(Player s)
	{
		for(Island i : virtualIslands.k())
		{
			if(i.getOwner().equals(s.getUniqueId()))
			{
				return virtualIslands.get(i);
			}
		}

		return null;
	}

	public static VirtualIsland getIsland(UUID id)
	{
		for(Island i : virtualIslands.k())
		{
			if(i.getId().equals(id))
			{
				return virtualIslands.get(i);
			}
		}

		return null;
	}

	public static IslandBuilder builder()
	{
		return new IslandBuilder();
	}

	public static class IslandBuilder
	{
		private UUID island;
		private Callback<Location> r;
		private Player stream;
		private int size;

		public IslandBuilder()
		{
			size = 8;
			this.island = UUID.randomUUID();

			if(engine.hasIslandById(island))
			{
				throw new RuntimeException("World already exists");
			}
		}

		public IslandBuilder size(int s)
		{
			this.size = s;

			return this;
		}

		public World create()
		{
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0.07, 0), 0, 500, 20);
			WorldCreator wc = new WorldCreator(worldName(island));
			Island is = new Island(island, stream.getUniqueId());
			wc.generator(new SkyGen());
			World w = wc.createWorld();
			w.setKeepSpawnInMemory(false);
			w.setTicksPerAnimalSpawns(100);
			w.setTicksPerMonsterSpawns(50);
			w.setDifficulty(Difficulty.HARD);
			w.setWaterAnimalSpawnLimit(0);
			w.setPVP(false);
			w.setTime(0);
			w.setGameRuleValue("announceAdvancements", "false");
			w.setGameRuleValue("commandBlocksEnabled", "false");
			w.setGameRuleValue("commandBlockOutput", "false");
			w.setGameRuleValue("disableElytraMovementCheck", "false");
			w.setGameRuleValue("doDaylightCycle", "true");
			w.setGameRuleValue("maxEntityCramming", "3");
			w.setGameRuleValue("randomTickSpeed", "2");
			w.setGameRuleValue("showDeathMessages", "false");
			w.setGameRuleValue("spawnRadius", "1");
			w.setGameRuleValue("spectatorsGenerateChunks", "false");
			Location ll = new Location(w, 0, 100, 0);
			w.getWorldBorder().setCenter(ll);
			w.getWorldBorder().setDamageAmount(2);
			w.getWorldBorder().setSize(299);
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0.11, 0), 0, 100, 20);
			virtualIslands.put(is, new VirtualIsland(w, is));
			engine.setIsland(is);
			ll.getChunk().load();
			IslandGenerator gen = new IslandGenerator(ll, (long) ((long) (Math.random() * 8423472229940949494l) + (Math.random() * 1999911123999444444L)));
			gen.setRadiusBlocks(size);
			Profiler pr = new Profiler();
			pr.begin();
			FinalInteger vi = new FinalInteger(0);
			new SR()
			{
				@Override
				public void run()
				{
					if(vi.get() == 0)
					{
						stream.sendTitle("", C.AQUA + "" + C.BOLD + gen.setStatus() + ": " + C.RESET + C.GRAY + F.pc(gen.getProgress(), 0), 0, 5, 20);
					}

					else
					{
						stream.sendTitle("", C.AQUA + "" + C.BOLD + "Done", 0, 5, 20);
						cancel();
					}
				}
			};

			gen.generate(new Callback<Integer>()
			{
				@SuppressWarnings("deprecation")
				@Override
				public void run(Integer t)
				{
					vi.set(1);
					pr.end();

					if(stream != null)
					{
						stream.sendMessage("Generated Island in " + C.WHITE + F.time(pr.getMilliseconds(), 1));
					}

					Location spawn = gen.getSpawn();

					for(Chunk i : w.getLoadedChunks())
					{
						i.unload(true, true);
					}

					w.save();

					if(r != null)
					{
						gen.getCt().flush();
						r.run(spawn);
					}
				}
			});

			return w;
		}

		public IslandBuilder streamProgress(Player p)
		{
			stream = p;

			return this;
		}

		public IslandBuilder onComplete(Callback<Location> r)
		{
			this.r = r;

			return this;
		}
	}

	public static String worldName(UUID island)
	{
		return "skydata/dimensions/" + island;
	}

	public static void saveAllWorlds()
	{
		for(VirtualIsland i : virtualIslands.v())
		{
			i.saveAll();
			i.unload();
		}
	}

	public static double getIslandsCount()
	{
		return virtualIslands.size();
	}

	public static void remove(Island island)
	{
		for(Island i : virtualIslands.k())
		{
			if(i.getId().equals(island.getId()))
			{
				virtualIslands.remove(i);
			}
		}
	}

	public static StorageEngine getStorageEngine()
	{
		return engine;
	}

	public static FileConfiguration getWorthConfig()
	{
		return fc;
	}

	public static void ensureIslandLoaded(Player player)
	{
		if(!hasIslandLoaded(player))
		{
			loadWorld(getIslandConfig(player));
		}
	}

	public static void coldDelete(Player player)
	{
		Island island = getIslandConfig(player);
		new File("skydata/deletions/" + island.getId().toString()).mkdirs();
		SkyMaster.getStorageEngine().removeIsland(island);
		VIO.delete(new File(SkyMaster.worldName(island.getId())));
	}
}
