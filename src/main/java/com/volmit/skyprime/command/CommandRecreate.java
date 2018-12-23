package com.volmit.skyprime.command;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandRecreate extends PawnCommand
{
	public CommandRecreate()
	{
		super("recreate", "reset");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		UUID island = sender.player().getUniqueId();

		if(!SkyMaster.hasIsland(island))
		{
			sender.sendMessage("You dont have an island. Use /sky create");
			return true;
		}

		sender.sendMessage("Deleting your island. Please Wait...");
		SkyMaster.deleteIsland(island, new Runnable()
		{
			@Override
			public void run()
			{
				sender.sendMessage("Island Deleted!");
				Bukkit.dispatchCommand(sender, "sky create");
			}
		});

		Bukkit.dispatchCommand(sender, "sky");

		return true;
	}
}
