package com.volmit.skyprime.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.skyprime.SkyMaster;

public class CommandReload extends PhantomCommand
{
	public CommandReload()
	{
		super("reload", "rld");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!sender.hasPermission("sky.reload"))
		{
			return true;
		}

		sender.sendMessage("Reloaded Configs.");
		SkyMaster.loadConfig();
		return true;
	}
}
