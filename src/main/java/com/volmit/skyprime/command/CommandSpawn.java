package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandSpawn extends PawnCommand
{
	public CommandSpawn()
	{
		super("spawn", "warp", "sp");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant spawn on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			Bukkit.dispatchCommand(sender.player(), "sky");
		}

		else
		{
			sender.sendMessage("Poof!");
			SkyMaster.getIsland(sender.player()).spawn(sender.player());
		}

		return true;
	}
}
