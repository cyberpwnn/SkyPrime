package com.volmit.skyprime.command;

import java.util.UUID;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandDelete extends PawnCommand
{
	public CommandDelete()
	{
		super("delete", "destroy");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		UUID island = sender.player().getUniqueId();

		if(!SkyMaster.hasIsland(island))
		{
			sender.sendMessage("You cant destroy an island you dont have. Use /sky create");
			return true;
		}

		sender.sendMessage("Deleting your island. Please Wait...");
		SkyMaster.deleteIsland(island, new Runnable()
		{
			@Override
			public void run()
			{
				sender.sendMessage("Island Deleted!");
			}
		});

		return true;
	}
}
