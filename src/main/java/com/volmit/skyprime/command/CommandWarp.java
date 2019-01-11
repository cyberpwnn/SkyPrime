package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.skyprime.SkyMaster;

public class CommandWarp extends PhantomCommand
{
	public CommandWarp()
	{
		super("warp", "sp");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant warp on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			Bukkit.dispatchCommand(sender.player(), "sky");
		}

		else
		{
			sender.sendMessage("Poof!");
			SkyMaster.getIsland(sender.player()).warp(sender.player());
		}

		return true;
	}
}
