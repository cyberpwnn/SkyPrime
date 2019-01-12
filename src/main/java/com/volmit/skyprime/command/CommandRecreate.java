package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.phantom.api.command.PhantomCommand;
import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.sheduler.S;
import com.volmit.skyprime.SkyMaster;

public class CommandRecreate extends PhantomCommand
{
	public CommandRecreate()
	{
		super("recreate");
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

		new S(5)
		{
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(sender.player(), "sky new");
			}
		};

		return true;
	}
}
