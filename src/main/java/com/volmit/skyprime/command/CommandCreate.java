package com.volmit.skyprime.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.lang.collections.Callback;

public class CommandCreate extends PawnCommand
{
	public CommandCreate()
	{
		super("create", "new");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You already have an island! Use /sky");
			return true;
		}

		sender.sendMessage("Creating a new Island for you!");
		SkyMaster.builder().streamProgress(sender.player()).onComplete(new Callback<Location>()
		{
			@Override
			public void run(Location t)
			{
				t.getWorld().setSpawnLocation(t);
				sender.sendMessage("Teleporting to " + t.getWorld().getName());
				sender.player().teleport(t);
				Bukkit.dispatchCommand(sender.player(), "sky");
			}
		}).create();

		return true;
	}
}
