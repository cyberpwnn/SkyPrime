package com.volmit.skyprime.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.skyprime.SkyMaster;

public class CommandDelete extends PhantomCommand
{
	public CommandDelete()
	{
		super("delete", "destroy");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
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
