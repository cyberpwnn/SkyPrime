package com.volmit.skyprime.command;

import org.bukkit.Bukkit;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.plugin.S;
import com.volmit.skyprime.SkyMaster;

public class CommandReboot extends PhantomCommand
{
	public CommandReboot()
	{
		super("reboot", "restart");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant reboot an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant reboot your island. It isnt loaded. Use /sky.");
			return true;
		}

		sender.sendMessage("Rebooting your island, please wait.");
		SkyMaster.getIsland(sender.player()).unload();

		new S(30)
		{
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(sender.player(), "sky");
			}
		};

		return true;
	}
}
