package io.shadowrealm.skyprime.gen;

import io.shadowrealm.skyprime.Config;
import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.storage.Island;
import mortar.api.sched.SR;
import mortar.compute.math.Profiler;
import mortar.lang.collection.Callback;
import mortar.lang.collection.FinalInteger;
import mortar.logic.format.F;
import mortar.util.text.C;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public class IslandBuilder implements Listener
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

		if(SkyMaster.engine.hasIslandById(island))
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
		WorldCreator wc = new WorldCreator(SkyMaster.worldName(island));
		Island is = new Island(island, stream.getUniqueId());
		is.setName(stream.getName() + "'s Island");
		is.setCompetitive(competitive);
		wc.environment(is.isCompetitive() ? World.Environment.NETHER : World.Environment.NORMAL);
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
		SkyMaster.virtualIslands.put(is, new VirtualIsland(w, is));
		SkyMaster.engine.setIsland(is);
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
					stream.sendTitle("", C.AQUA + "" + C.BOLD + gen.getStatus() + ": " + C.RESET + C.GRAY + F.pc(gen.getProgress(), 0), 0, 5, 20);
				}

				else
				{
					stream.sendTitle("", C.AQUA + "" + C.BOLD + "Done", 0, 5, 20);
					cancel();
					r.run(gen.getSpawn());
					//is.setMinsize(gen.getFurthest());
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
