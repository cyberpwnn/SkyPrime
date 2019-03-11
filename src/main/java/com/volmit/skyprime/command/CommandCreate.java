package com.volmit.skyprime.command;

import org.bukkit.Location;

import com.volmit.skyprime.Config;
import com.volmit.skyprime.SkyMaster;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandCreate extends MortarCommand
{
	public CommandCreate()
	{
		super("create", "new");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You already have an island! Use /sky");
			return true;
		}

		int size = Config.ISLAND_SIZE;

		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("small") || args[0].equalsIgnoreCase("tiny") || args[0].equalsIgnoreCase("mini") || args[0].equalsIgnoreCase("little"))
			{
				size = Config.ISLAND_SIZE_SMALL;
			}

			else if(args[0].equalsIgnoreCase("big") || args[0].equalsIgnoreCase("large") || args[0].equalsIgnoreCase("huge") || args[0].equalsIgnoreCase("macro"))
			{
				size = Config.ISLAND_SIZE_BIG;
			}

			else
			{
				sender.sendMessage("/sky new [big/small]");
				sender.sendMessage("No parameter = medium");
			}
		}

		sender.sendMessage("Creating a new Island for you!");
		SkyMaster.builder().size(size).competitive(false).streamProgress(sender.player()).onComplete(new Callback<Location>()
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
