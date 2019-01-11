package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.skyprime.SkyMaster;

public class CommandSpawn extends PhantomCommand
{
	public CommandSpawn()
	{
		super("spawn", "sp");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
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
