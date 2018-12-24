package com.volmit.skyprime;

import com.volmit.skyprime.command.CommandSkyPrime;
import com.volmit.skyprime.storage.FileStorageEngine;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;

@CommandTag("&8[&bSKY&8]&7: ")
public class SkyPrime extends VolumePlugin
{
	@Command
	private CommandSkyPrime commandsp;

	@Start
	public void start()
	{
		SkyMaster.setStorageEngine(new FileStorageEngine(getDataFolder("data")));
		SkyMaster.deleteMarkedWorlds();
	}

	@Stop
	public void stop()
	{

	}
}
