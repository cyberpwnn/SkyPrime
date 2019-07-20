package io.shadowrealm.skyprime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.shadowrealm.skyprime.storage.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.shadowrealm.skyprime.gen.IslandGenerator;
import io.shadowrealm.skyprime.gen.SkyGen;
import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.StorageEngine;
import mortar.api.nms.ChunkTracker;
import mortar.api.sched.SR;
import mortar.compute.math.Profiler;
import mortar.lang.collection.Callback;
import mortar.lang.collection.FinalInteger;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.logic.io.VIO;
import mortar.util.text.C;
import mortar.util.text.D;

public class SkyMaster
{
	private static StorageEngine engine;
	private static GMap<Island, VirtualIsland> virtualIslands = new GMap<>();
	private static FileConfiguration fc;
	private static GMap<World, ChunkTracker> ctx = new GMap<>();
	private static GMap<String, Integer> sizemap = new GMap<>();

	public static int getSizeFor(Player p)
	{
		int max = Config.SIZE_DEFAULT_BARRIER;

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
		File f = SkyPrime.instance.getDataFile("worth.yml");
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

	public static void loadConfig()
	{
		try
		{
			Config.load();
		}

		catch(IllegalArgumentException | IllegalAccessException | IOException e)
		{
			e.printStackTrace();
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
	public static void unloadWorldRightFuckingNow(World world, boolean save)
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

	public static void unloadWorld(World world, boolean save)
	{
		unloadWorldRightFuckingNow(world, save);
	}

	private static void loadWorld(Island is)
	{
		System.out.println("world " + worldName(is.getId()));
		WorldCreator ww = new WorldCreator(worldName(is.getId()));
		ww.generator(new SkyGen());
		ww.environment(is.isCompetitive() ? Environment.NETHER : Environment.NORMAL);
		ww.generateStructures(false);
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

	public static void flushTracker(World w)
	{
		ChunkTracker c = ctx.get(w);

		if(c != null)
		{
			ctx.remove(w);
			c.flush();
		}
	}

	private static boolean hasIslandLoaded(UUID uuid)
	{
		return getIsland(uuid) != null;
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

	public static VirtualIsland getPlayerActiveIsland(Player player)
	{
		for (VirtualIsland is : virtualIslands.v()) {
			if (is.getWorld().equals(player.getWorld())) return is;
		}
		return getIsland(player);
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

	public static class IslandBuilder implements Listener
	{
		private UUID island;
		private Callback<Location> r;
		private Player stream;
		private boolean competitive;
		private int size;

		public IslandBuilder()
		{
			this.island = UUID.randomUUID();
			competitive = false;
			size = Config.ISLAND_SIZE;

			if(engine.hasIslandById(island))
			{
				throw new RuntimeException("World already exists");
			}
		}

		public IslandBuilder size(int size)
		{
			this.size = size;
			return this;
		}

		public IslandBuilder competitive(boolean c)
		{
			competitive = c;
			return this;
		}

		public World create()
		{
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0.07, 0), 0, 500, 20);
			WorldCreator wc = new WorldCreator(worldName(island));
			Island is = new Island(island, stream.getUniqueId());
			is.setVisibility(Config.ISLAND_IS_PRIVATE ? Visibility.PRIVATE : Visibility.PUBLIC);
			is.setName(stream.getName() + "'s Island");
			is.setCompetitive(competitive);
			wc.environment(is.isCompetitive() ? Environment.NETHER : Environment.NORMAL);
			wc.generateStructures(false);
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
			w.setGameRuleValue("maxEntityCramming", "8");
			w.setGameRuleValue("randomTickSpeed", "2");
			w.setGameRuleValue("showDeathMessages", "false");
			w.setGameRuleValue("spawnRadius", "1");
			w.setGameRuleValue("spectatorsGenerateChunks", "false");
			Location ll = new Location(w, 0, 100, 0);
			w.getWorldBorder().setCenter(ll);
			w.getWorldBorder().setDamageAmount(2);
			w.getWorldBorder().setSize(is.isCompetitive() ? 10 : 300);
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0.11, 0), 0, 100, 20);
			virtualIslands.put(is, new VirtualIsland(w, is));
			engine.setIsland(is);
			ll.getChunk().load();
			IslandGenerator gen = new IslandGenerator(ll, (long) (is.isCompetitive() ? 1337 : ((long) (Math.random() * 8423472229940949494l) + (Math.random() * 1999911123999444444L))));

			if(is.isCompetitive())
			{
				gen.setSquashTop(1.4);
				gen.setSeed(1337);
				gen.setRadiusBlocks(Math.max(3, Config.ISLAND_COMP_SIZE));
			}

			else
			{
				gen.setRadiusBlocks(size);

				if(size > 8)
				{
					gen.setOctaves(8);
					gen.setNoise(size / 3);
					gen.setSquashTop(1.4);
					gen.setFrequency(0.3);
					gen.setAmplifier(0.5);
					gen.setDimension(0.5);
				}
			}

			Profiler pr = new Profiler();
			pr.begin();
			FinalInteger vi = new FinalInteger(0);
			System.out.println("Size is " + size);
			gen.generate(new Callback<Integer>()
			{
				@Override
				public void run(Integer t)
				{
					vi.set(10);
				}
			});

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
						r.run(gen.getSpawn());
						is.setMinsize(gen.getFurthest());
					}
				}
			};

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

	protected static void putCT(World w, ChunkTracker ct)
	{
		ctx.put(w, ct);
	}

	public static void saveAllWorlds()
	{
		for(VirtualIsland i : virtualIslands.v())
		{
			D.as("SkyMaster").l("Saving " + i.getWorld().getName());
			i.saveAll();
			D.as("SkyMaster").l("Unloading " + i.getWorld().getName());
			i.unload();
		}

		virtualIslands.clear();
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

	public static void ensureIslandLoaded(UUID uuid)
	{
		if(!hasIslandLoaded(uuid))
		{
			loadWorld(getIslandConfig(uuid));
		}
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

	public static String getVoltageSummary()
	{
		double max = 0;
		double use = 0;

		for(VirtualIsland i : virtualIslands.v())
		{
			max += i.getTotalVolts();
			use += i.getUsedVolts();
		}

		return C.WHITE + F.f(use, 0) + " / " + F.f(max, 0) + C.GRAY + " (" + F.time(Voltage.getMilliseconds(use), 2) + ")";
	}
}
