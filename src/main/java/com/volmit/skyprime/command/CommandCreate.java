package com.volmit.skyprime.command;

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
				Location sp = t.clone();
				t.getWorld().setSpawnLocation(sp);
				sender.sendMessage("Loading your island.");
				SkyMaster.getIsland(sender.player()).spawn(sender.player());
			}
		}).create();

		return true;
	}
}
