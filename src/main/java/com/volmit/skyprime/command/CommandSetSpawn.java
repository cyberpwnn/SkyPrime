package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandSetSpawn extends MortarCommand
{
	public CommandSetSpawn()
	{
		super("setspawn");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant set spawn on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant set spawn on your island. It isnt loaded. Use /sky.");
			return true;
		}

		if(!SkyMaster.getIsland(sender.player()).getWorld().equals(sender.player().getWorld()))
		{
			sender.sendMessage("You cant set spawn on your island. You aren't in it. Use /sky.");
			return true;
		}

		SkyMaster.getIsland(sender.player()).setSpawn(sender.player());
		sender.sendMessage("Spawn Updated to your position.");

		return true;
	}
}
