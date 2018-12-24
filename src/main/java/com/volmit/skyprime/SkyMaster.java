package com.volmit.skyprime;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import com.volmit.skyprime.gen.IslandGenerator;
import com.volmit.skyprime.gen.SkyGen;
import com.volmit.skyprime.storage.Island;
import com.volmit.skyprime.storage.StorageEngine;
import com.volmit.volume.bukkit.task.SR;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.collections.Callback;
import com.volmit.volume.lang.collections.FinalInteger;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.io.VIO;
import com.volmit.volume.math.Profiler;

public class SkyMaster
{
	public static GMap<UUID, World> worlds = new GMap<>();
	private static StorageEngine engine;
	
	public static void setStorageEngine(StorageEngine e)
	{
		engine = e;
	}
	
	public static void deleteMarkedWorlds()
	{
		File f = new File("delete");
		
		if(f.exists())
		{
			for(File i : f.listFiles())
			{
				File x = new File(worldName(UUID.fromString(i.getName())));
				VIO.delete(x);
			}
		}
		
		VIO.delete(f);
	}

	public static void markForDeletion(UUID island)
	{
		new File(new File("delete"), island.toString()).mkdirs();
	}
	
	public static String worldName(UUID island)
	{
		return "sky-" + island;
	}
	
	public static boolean hasPersonalIsland(UUID p)
	{
		return engine.hasPersonalIsland(p);
	}

	public static Island getIsland(UUID p)
	{
		return engine.getPersonalIsland(p);
	}
	
	public static IslandBuilder builder()
	{
		return new IslandBuilder();
	}

	public static boolean isIslandLoaded(UUID id)
	{
		return worlds.containsKey(id);
	}

	public static File islandFolder(UUID id)
	{
		return new File(worldName(id));
	}

	public static void deleteIsland(UUID player, Runnable onDeleted)
	{
		if(isIslandLoaded(engine.getPersonalIsland(player).getId()))
		{
			unloadIsland(engine.getPersonalIsland(player).getId(), false);
		}

		markForDeletion(engine.getPersonalIsland(player).getId());
		engine.removePersonalIsland(player);
		onDeleted.run();
	}

	public static void unloadIsland(UUID id)
	{
		unloadIsland(id, true);
	}

	@SuppressWarnings("deprecation")
	public static void unloadIsland(UUID id, boolean save)
	{
		if(worlds.containsKey(id))
		{
			try
			{
				World w = worlds.get(id);
				for(Player i : w.getPlayers())
				{
					i.sendMessage("You are being teleported off this world. It's crumbling!");
					i.teleport(Bukkit.getWorld("world").getSpawnLocation());
				}

				for(Chunk i : w.getLoadedChunks())
				{
					i.unload(false, false);
				}

				if(Bukkit.unloadWorld(w, save))
				{
					worlds.remove(id);
				}

				System.gc();
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	public static World getWorld(UUID id)
	{
		if(!isIslandLoaded(id))
		{
			loadIsland(id);
		}

		return worlds.get(id);
	}

	public static void loadIsland(UUID id)
	{
		if(!worlds.containsKey(id))
		{
			try
			{
				worlds.put(id, Bukkit.getWorld(worldName(id)));
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	public static boolean hasIsland(UUID id)
	{
		System.out.println(islandFolder(id).getAbsolutePath());

		return islandFolder(id).exists();
	}

	public static class IslandBuilder
	{
		private UUID island;
		private Callback<Location> r;
		private Player stream;
		private int size;

		public IslandBuilder()
		{
			size = 7;
			this.island = UUID.randomUUID();

			if(hasIsland(island))
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
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0, 0), 0, 500, 20);
			WorldCreator wc = new WorldCreator(worldName(island));
			wc.generator(new SkyGen());
			World w = wc.createWorld();
			w.setKeepSpawnInMemory(false);
			w.setTicksPerAnimalSpawns(100);
			w.setTicksPerMonsterSpawns(50);
			w.setDifficulty(Difficulty.HARD);
			w.setWaterAnimalSpawnLimit(0);
			w.setPVP(false);
			w.setTime(0);
			stream.sendTitle("", C.AQUA + "" + C.BOLD + "Generating: " + C.RESET + C.GRAY + F.pc(0.11, 0), 0, 100, 20);
			worlds.put(island, w);
			Location ll = new Location(w, 0, size * 2, 0);
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

					if(r != null)
					{
						Island i = new Island(island, stream.getUniqueId());
						engine.setPersonalIsland(stream.getUniqueId(), i);
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
}
