package com.volmit.skyprime.command;

import java.util.concurrent.TimeUnit;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.lang.F;
import com.volmit.phantom.api.math.M;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.skyprime.SkyMaster;

public class CommandSave extends PhantomCommand
{
	public CommandSave()
	{
		super("save", "sav");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
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
