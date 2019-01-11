package com.volmit.skyprime.command;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.imp.command.PhantomCommand;
import com.volmit.skyprime.SkyMaster;

public class CommandSetWarp extends PhantomCommand
{
	public CommandSetWarp()
	{
		super("setwarp");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant set warp on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant set warp on your island. It isnt loaded. Use /sky.");
			return true;
		}

		if(!SkyMaster.getIsland(sender.player()).getWorld().equals(sender.player().getWorld()))
		{
			sender.sendMessage("You cant set warp on your island. You aren't in it. Use /sky.");
			return true;
		}

		SkyMaster.getIsland(sender.player()).setWarp(sender.player());
		sender.sendMessage("Public Warp Updated to your position.");

		return true;
	}
}
