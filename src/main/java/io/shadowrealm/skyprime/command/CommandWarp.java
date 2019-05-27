package io.shadowrealm.skyprime.command;

import org.bukkit.Bukkit;

import io.shadowrealm.skyprime.SkyMaster;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandWarp extends MortarCommand
{
	public CommandWarp()
	{
		super("warp", "sp");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
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
