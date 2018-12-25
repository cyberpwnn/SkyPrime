package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;

public class CommandSetSpawn extends PawnCommand
{
	public CommandSetSpawn()
	{
		super("setspawn", "setwarp");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
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
		SkyMaster.getIsland(sender.player()).save();

		return true;
	}
}
