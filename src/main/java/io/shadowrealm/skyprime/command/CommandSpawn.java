package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.skyprime.SkyMaster;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandSpawn extends MortarCommand
{
	public CommandSpawn()
	{
		super("spawn", "sp");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
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
