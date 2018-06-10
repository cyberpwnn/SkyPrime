package com.volmit.skyprime.service;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;

import com.volmit.skyprime.api.SkyWorld;
import com.volmit.skyprime.world.SWorld;
import com.volmit.skyprime.world.gen.SkyGen;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.service.IService;
import com.volmit.volume.lang.collections.GMap;

public class SPWorldSVC implements IService
{
	private GMap<World, SkyWorld> worlds;

	@Start
	public void onStart()
	{
		worlds = new GMap<World, SkyWorld>();
	}

	@Stop
	public void onStop()
	{

	}

	public SkyWorld createWorld(String name, int gridSize, int spawnRadius)
	{
		WorldCreator wc = new WorldCreator(name);
		wc.generator(new SkyGen(gridSize, spawnRadius));
		World w = wc.createWorld();
		SkyWorld sw = new SWorld(w);
		worlds.put(w, sw);

		return sw;
	}

	@EventHandler
	public void on(WorldLoadEvent e)
	{

	}
}
