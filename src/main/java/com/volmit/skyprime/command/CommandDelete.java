package com.volmit.skyprime.command;

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
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant destroy an island you dont have. Use /sky create");
			return true;
		}

		if(SkyMaster.hasIslandLoaded(sender.player()))
		{
			SkyMaster.getIsland(sender.player()).delete();
		}

		else
		{
			SkyMaster.coldDelete(sender.player());
		}

		sender.sendMessage("Island Deleted!");

		return true;
	}
}
