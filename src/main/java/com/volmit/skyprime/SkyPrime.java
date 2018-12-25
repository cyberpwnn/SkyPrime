package com.volmit.skyprime;

import java.io.File;

import com.volmit.skyprime.command.CommandSkyPrime;
import com.volmit.skyprime.storage.FileStorageEngine;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.Command;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.task.SR;

@CommandTag("&8[&bSKY&8]&7: ")
public class SkyPrime extends VolumePlugin
{
	@Command
	private CommandSkyPrime commandsp;

	@Start
	public void start()
	{
		SkyMaster.setStorageEngine(new FileStorageEngine(new File("skydata/islands")));
		SkyMaster.deleteIslands();

		new SR(20)
		{
			@Override
			public void run()
			{
				SkyMaster.tick();
			}
		};
	}

	@Stop
	public void stop()
	{
		SkyMaster.saveAllWorlds();
	}
}
