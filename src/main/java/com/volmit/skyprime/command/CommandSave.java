package com.volmit.skyprime.command;

import java.util.concurrent.TimeUnit;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.math.M;

public class CommandSave extends PawnCommand
{
	public CommandSave()
	{
		super("save", "sav");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant save an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant save your island. It isnt loaded. Use /sky.");
			return true;
		}

		if(M.ms() - SkyMaster.getIsland(sender.player()).getIsland().getLastSave() > TimeUnit.MINUTES.toMillis(5))
		{
			SkyMaster.getIsland(sender.player()).saveAll();
			sender.sendMessage("Island Saved");
		}

		else
		{
			sender.sendMessage("Your island was just saved " + F.timeLong(M.ms() - SkyMaster.getIsland(sender.player()).getIsland().getLastSave(), 0) + " ago.");
		}

		return true;
	}
}
