package io.shadowrealm.skyprime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.shadowrealm.skyprime.gen.IslandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.shadowrealm.skyprime.gen.SkyGen;
import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.StorageEngine;
import mortar.api.nms.ChunkTracker;
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

	public static VirtualIsland getIsland(World w)
	{
		for (VirtualIsland vi : virtualIslands.values()) {
			if (vi.getWorld().equals(w)) return vi;
		}
		return null;
	}

	public static int getSizeFor(Player p)
	{
		int max = Config.SIZE_DEFAULT_BARRIER;

		for(String i : Config.SIZE_RANKS.keySet())
		{
			if(p.hasPermission("sky.size." + i.toLowerCase()) && Config.SIZE_RANKS.get(i) > max)
			{
				max = Config.SIZE_RANKS.get(i);
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
